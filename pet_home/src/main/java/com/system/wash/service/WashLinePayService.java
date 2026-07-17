package com.system.wash.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import com.system.wash.config.WashLinePayProperties;
import com.system.wash.entity.WashAppointmentBean;
import com.system.wash.entity.WashLinePayCallbackBean;
import com.system.wash.entity.WashLinePayTransactionBean;
import com.system.wash.entity.WashLinePayRefundBean;
import com.system.wash.entity.WashPaymentBean;
import com.system.wash.repository.WashLinePayCallbackRepository;
import com.system.wash.repository.WashLinePayTransactionRepository;
import com.system.wash.repository.WashLinePayRefundRepository;
import com.system.wash.repository.WashPaymentRepository;
import com.system.wash.repository.WashAppointmentRepository;
import com.system.member.service.MailService;
import com.system.member.repository.UsersRepository;
import com.system.member.entity.UserBean;
import org.springframework.beans.factory.annotation.Autowired;
import com.system.wash.repository.WashApptDetailRepository;
import com.system.wash.repository.WashPackageServiceItemRepository;
import com.system.wash.entity.WashApptDetailBean;

@Service
public class WashLinePayService {

    private static final Logger log = LoggerFactory.getLogger(WashLinePayService.class);

    private static final String REQUEST_URI = "/v3/payments/request";

    private final WashLinePayProperties properties;
    private final WashPaymentRepository washPaymentRepository;
    private final WashAppointmentRepository washAppointmentRepository;
    private final WashLinePayTransactionRepository transactionRepository;
    private final WashLinePayCallbackRepository callbackRepository;
    private final WashLinePayRefundRepository refundRepository;
    private final RestClient restClient;

    @Autowired
    private MailService mailService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private WashApptDetailRepository washApptDetailRepository;

    @Autowired
    private WashPackageServiceItemRepository washPackageServiceItemRepository;

    public WashLinePayService(
            WashLinePayProperties properties,
            WashPaymentRepository washPaymentRepository,
            WashAppointmentRepository washAppointmentRepository,
            WashLinePayTransactionRepository transactionRepository,
            WashLinePayCallbackRepository callbackRepository,
            WashLinePayRefundRepository refundRepository) {
        this.properties = properties;
        this.washPaymentRepository = washPaymentRepository;
        this.washAppointmentRepository = washAppointmentRepository;
        this.transactionRepository = transactionRepository;
        this.callbackRepository = callbackRepository;
        this.refundRepository = refundRepository;
        this.restClient = RestClient.builder()
                .baseUrl(properties.getApiBaseUrl())
                .build();
    }

    @Transactional
    public JSONObject requestPayment(Integer paymentId) {
        validateConfig();

        WashPaymentBean payment = washPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("paymentId not found: " + paymentId));

        WashLinePayTransactionBean existing = transactionRepository
                .findFirstByPaymentPaymentIdOrderByLinepayTxnIdDesc(paymentId)
                .orElse(null);
        if (existing != null && StringUtils.hasText(existing.getPaymentUrl()) && Byte.valueOf((byte) 0).equals(existing.getTradeStatus())) {
            return buildRequestResult(existing);
        }

        String orderId = buildOrderId(payment);
        String baseOrderId = orderId;
        int suffix = 1;
        while (transactionRepository.findByOrderId(orderId).isPresent()) {
            orderId = baseOrderId + "_" + suffix;
            suffix++;
        }

        String nonce = UUID.randomUUID().toString();
        JSONObject requestBody = buildRequestBody(payment, orderId);
        String body = requestBody.toString();
        String signature = sign(REQUEST_URI, body, nonce);

        System.out.println("=== LINE Pay Request API Request ===");
        System.out.println("URI: " + REQUEST_URI);
        System.out.println("Headers: ChannelId=" + properties.getChannelId() + ", Nonce=" + nonce + ", Signature=" + signature);
        System.out.println("Body: " + body);

        String responseBody = restClient.post()
                .uri(REQUEST_URI)
                .header("Content-Type", "application/json")
                .header("X-LINE-ChannelId", properties.getChannelId())
                .header("X-LINE-Authorization-Nonce", nonce)
                .header("X-LINE-Authorization", signature)
                .body(body)
                .retrieve()
                .body(String.class);

        System.out.println("=== LINE Pay Request API Response ===");
        System.out.println("Response: " + responseBody);

        JSONObject response = new JSONObject(responseBody);
        WashLinePayTransactionBean txn = new WashLinePayTransactionBean();
        txn.setPayment(payment);
        txn.setOrderId(orderId);
        txn.setProductName(buildProductName(payment));
        txn.setAmount(payment.getAmount());
        txn.setCurrency(properties.getCurrency());
        txn.setChannelId(properties.getChannelId());
        txn.setNonce(nonce);
        txn.setSignature(signature);
        txn.setRequestUri(REQUEST_URI);
        txn.setPaymentType(payment.getPaymentMethod());
        txn.setTradeStatus("0000".equals(response.optString("returnCode")) ? (byte) 0 : (byte) 2);
        txn.setCreatedAt(LocalDateTime.now());
        txn.setUpdatedAt(LocalDateTime.now());

        JSONObject info = response.optJSONObject("info");
        if (info != null) {
            txn.setTransactionId(info.has("transactionId") ? info.optLong("transactionId") : null);
            JSONObject paymentUrl = info.optJSONObject("paymentUrl");
            if (paymentUrl != null) {
                txn.setPaymentUrl(paymentUrl.optString("web", null));
            }
        }
        transactionRepository.save(txn);

        if (txn.getTransactionId() != null) {
            log.info("[LINE PAY] Payment Requested. PaymentId: {}, OrderId: {}, TransactionId: {}, Amount: {}, PaymentUrl: {}",
                    paymentId, orderId, txn.getTransactionId(), txn.getAmount(), txn.getPaymentUrl());
        }

        if (!"0000".equals(response.optString("returnCode"))) {
            throw new IllegalStateException(response.optString("returnMessage", "LINE Pay request failed"));
        }

        return buildRequestResult(txn);
    }

    @Transactional
    public JSONObject confirmPayment(String orderId, Long transactionId) {
        validateConfig();

        WashLinePayTransactionBean txn = transactionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("orderId not found: " + orderId));

        Long lineTransactionId = transactionId != null ? transactionId : txn.getTransactionId();
        if (lineTransactionId == null) {
            throw new IllegalArgumentException("transactionId is required");
        }

        String uri = "/v3/payments/" + lineTransactionId + "/confirm";
        String nonce = UUID.randomUUID().toString();
        JSONObject bodyJson = new JSONObject()
                .put("amount", txn.getAmount())
                .put("currency", txn.getCurrency());
        String body = bodyJson.toString();
        String signature = sign(uri, body, nonce);

        System.out.println("=== LINE Pay Confirm API Request ===");
        System.out.println("URI: " + uri);
        System.out.println("Headers: ChannelId=" + properties.getChannelId() + ", Nonce=" + nonce + ", Signature=" + signature);
        System.out.println("Body: " + body);

        log.info("[LINE PAY] Confirming Payment. OrderId: {}, TransactionId: {}, Amount: {}",
                orderId, lineTransactionId, txn.getAmount());

        String responseBody = restClient.post()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("X-LINE-ChannelId", properties.getChannelId())
                .header("X-LINE-Authorization-Nonce", nonce)
                .header("X-LINE-Authorization", signature)
                .body(body)
                .retrieve()
                .body(String.class);

        System.out.println("=== LINE Pay Confirm API Response ===");
        System.out.println("Response: " + responseBody);

        JSONObject response = new JSONObject(responseBody);
        boolean success = "0000".equals(response.optString("returnCode"));

        log.info("[LINE PAY] Payment Confirm Response. OrderId: {}, TransactionId: {}, Success: {}, ReturnCode: {}, ReturnMessage: {}",
                orderId, lineTransactionId, success, response.optString("returnCode"), response.optString("returnMessage"));
        saveCallback(txn, response, responseBody, (byte) 1, success);

        txn.setTransactionId(lineTransactionId);
        txn.setTradeStatus(success ? (byte) 1 : (byte) 2);
        txn.setConfirmedAt(success ? LocalDateTime.now() : null);
        txn.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(txn);

        WashPaymentBean payment = txn.getPayment();
        if (payment != null) {
            LocalDateTime confirmedAt = LocalDateTime.now();
            payment.setTransactionNo(String.valueOf(lineTransactionId));
            payment.setPaymentStatus(success ? (byte) 1 : (byte) 2);
            payment.setPaidAt(success ? confirmedAt : null);
            boolean isDep = success && isDepositPayment(payment);
            if (isDep) {
                payment.setPayNo(buildOrderPayNo(payment));
                autoConvertAppointmentToOrder(payment, confirmedAt);
            }
            washPaymentRepository.save(payment);

            if (isDep) {
                sendSuccessEmail(payment);
            }
        }

        return response;
    }

    private void sendSuccessEmail(WashPaymentBean payment) {
        try {
            if (payment == null || payment.getApptOrder() == null) {
                return;
            }
            WashAppointmentBean appointment = payment.getApptOrder();
            Long memberId = appointment.getMemberId();
            if (memberId == null) {
                log.warn("[EMAIL] No member ID found for appointment.");
                return;
            }
            UserBean user = usersRepository.findById(memberId).orElse(null);
            if (user == null || !StringUtils.hasText(user.getEmail())) {
                log.warn("[EMAIL] User or email not found for memberId: {}", memberId);
                return;
            }
            String email = user.getEmail();

            // Get nickname from user profile
            String nickname = "家長";
            if (user.getProfiles() != null && StringUtils.hasText(user.getProfiles().getNickname())) {
                nickname = user.getProfiles().getNickname();
            } else if (user.getProfiles() != null && StringUtils.hasText(user.getProfiles().getFullName())) {
                nickname = user.getProfiles().getFullName();
            }

            String orderNo = payment.getPayNo() != null ? payment.getPayNo() : "";
            String transactionNo = payment.getTransactionNo() != null ? payment.getTransactionNo() : "";
            int deposit = payment.getAmount() != null ? payment.getAmount() : 200;

            // Generate detail rows
            List<WashApptDetailBean> details = washApptDetailRepository.findByApptOrderApptOrderId(appointment.getApptOrderId());
            StringBuilder tableRows = new StringBuilder();
            int subtotalAmount = 0;
            if (details != null) {
                for (WashApptDetailBean detail : details) {
                    String serviceName = "";
                    boolean isPackage = false;
                    if (detail.getService() != null) {
                        serviceName = detail.getService().getServiceName();
                        isPackage = washPackageServiceItemRepository.existsByServiceServiceIdAndTypeCode(detail.getService().getServiceId(), "PACKAGE");
                    }
                    if (detail.getPackageServiceItem() != null) {
                        if (detail.getPackageServiceItem().getService() != null) {
                            serviceName = detail.getPackageServiceItem().getService().getServiceName();
                        } else {
                            serviceName = detail.getPackageServiceItem().getTypeName();
                        }
                        isPackage = true;
                    }

                    String typeLabel = isPackage ? "套餐服務" : "基礎服務";
                    int unitPrice = detail.getUnitPrice() != null ? detail.getUnitPrice() : 0;
                    int itemAmount = detail.getItemAmount() != null ? detail.getItemAmount() : 0;
                    subtotalAmount += itemAmount;

                    tableRows.append("                    <tr>\n")
                             .append("                        <td class=\"item-name\">").append(typeLabel).append(": ").append(serviceName).append("</td>\n")
                             .append("                        <td class=\"text-right\" style=\"color: #999;\">$ ").append(unitPrice).append("</td>\n")
                             .append("                        <td class=\"text-right price-highlight\">$ ").append(itemAmount).append("</td>\n")
                             .append("                    </tr>\n");
                }
            }

            int subtotal = appointment.getSubtotalAmount() != null ? appointment.getSubtotalAmount() : subtotalAmount;
            int remaining = subtotal - deposit;
            if (remaining < 0) remaining = 0;
            String note = appointment.getNote() != null && !appointment.getNote().trim().isEmpty() ? appointment.getNote() : "無備註事項";

            String formattedSubtotal = String.format("%,d", subtotal);
            String formattedDeposit = String.format("%,d", deposit);
            String formattedRemaining = String.format("%,d", remaining);

            String formattedApptTime = "—";
            if (appointment.getApptDate() != null) {
                String dateStr = appointment.getApptDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String timeStr = "";
                if (appointment.getApptStartTime() != null) {
                    timeStr = " " + appointment.getApptStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                }
                formattedApptTime = dateStr + timeStr;
            }

            String subject = "【毛起來】叮咚！預約成功確認信：準備給毛孩一場最讚的享受吧！✨";
            String template = getEmailHtmlTemplate();

            String htmlContent = template
                    .replace("user_profiles.nickname", nickname)
                    .replace("ODR-20260629-3-002", orderNo)
                    .replace("2026062902364691510", transactionNo)
                    .replace("2026-06-29 09:00", formattedApptTime)
                    .replace("NT$ 1,050", "NT$ " + formattedSubtotal)
                    .replace("- NT$ 200", "- NT$ " + formattedDeposit)
                    .replace("NT$ 850", "NT$ " + formattedRemaining)
                    .replace("無備註事項", note);

            // Replace the mock rows in tbody with dynamic rows
            String searchTbodyStart = "<tbody>";
            String searchTbodyEnd = "</tbody>";
            int tbodyStartIndex = htmlContent.indexOf(searchTbodyStart);
            int tbodyEndIndex = htmlContent.indexOf(searchTbodyEnd, tbodyStartIndex);
            if (tbodyStartIndex != -1 && tbodyEndIndex != -1) {
                htmlContent = htmlContent.substring(0, tbodyStartIndex + searchTbodyStart.length()) + "\n" +
                              tableRows.toString() +
                              htmlContent.substring(tbodyEndIndex);
            }

            log.info("[EMAIL] Sending reservation success HTML email to: {}, Nickname: {}, OrderNo: {}, TxnNo: {}, Deposit: {}, Remaining: {}",
                    email, nickname, orderNo, transactionNo, deposit, remaining);
            mailService.sendHtmlEmail(email, subject, htmlContent);
            log.info("[EMAIL] Email sent successfully.");
        } catch (Exception e) {
            log.error("[EMAIL] Failed to send reservation success email: {}", e.getMessage(), e);
        }
    }

    private String getEmailHtmlTemplate() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"zh-TW\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>毛起來 - 預約成功通知</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: \"Helvetica Neue\", Helvetica, Arial, \"PingFang TC\", \"Hiragino Sans GB\", \"Microsoft JhengHei\", sans-serif;\n" +
                "            background-color: #f9f6f0;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            -webkit-font-smoothing: antialiased;\n" +
                "        }\n" +
                "        .email-container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 40px auto;\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 16px;\n" +
                "            overflow: hidden;\n" +
                "            box-shadow: 0 4px 15px rgba(161, 131, 102, 0.1);\n" +
                "        }\n" +
                "        .header {\n" +
                "            background-color: #bf9d7a;\n" +
                "            padding: 30px;\n" +
                "            text-align: center;\n" +
                "            color: #ffffff;\n" +
                "        }\n" +
                "        .header h1 {\n" +
                "            margin: 10px 0 0 0;\n" +
                "            font-size: 24px;\n" +
                "            font-weight: 600;\n" +
                "            letter-spacing: 1px;\n" +
                "        }\n" +
                "        .content {\n" +
                "            padding: 40px 30px;\n" +
                "            color: #4a4a4a;\n" +
                "            line-height: 1.6;\n" +
                "        }\n" +
                "        .greeting {\n" +
                "            font-size: 18px;\n" +
                "            font-weight: bold;\n" +
                "            color: #7c5d3f;\n" +
                "            margin-bottom: 15px;\n" +
                "        }\n" +
                "        .status-badge {\n" +
                "            display: inline-block;\n" +
                "            background-color: #eaf5ea;\n" +
                "            color: #4caf50;\n" +
                "            padding: 6px 16px;\n" +
                "            border-radius: 20px;\n" +
                "            font-weight: bold;\n" +
                "            font-size: 14px;\n" +
                "            margin-bottom: 25px;\n" +
                "        }\n" +
                "        .section-title {\n" +
                "            font-size: 16px;\n" +
                "            font-weight: bold;\n" +
                "            color: #7c5d3f;\n" +
                "            margin-top: 25px;\n" +
                "            margin-bottom: 12px;\n" +
                "        }\n" +
                "        .info-card {\n" +
                "            background-color: #fdfbf7;\n" +
                "            border: 1px solid #f0e6da;\n" +
                "            border-radius: 12px;\n" +
                "            padding: 20px;\n" +
                "            margin-bottom: 25px;\n" +
                "        }\n" +
                "        .info-row {\n" +
                "            display: flex;\n" +
                "            justify-content: space-between;\n" +
                "            padding: 10px 0;\n" +
                "            border-bottom: 1px dashed #eaddcd;\n" +
                "        }\n" +
                "        .info-row:last-child {\n" +
                "            border-bottom: none;\n" +
                "        }\n" +
                "        .info-label {\n" +
                "            color: #8c7662;\n" +
                "            font-weight: 500;\n" +
                "        }\n" +
                "        .info-value {\n" +
                "            color: #333333;\n" +
                "            font-weight: bold;\n" +
                "            text-align: right;\n" +
                "        }\n" +
                "        .detail-table {\n" +
                "            width: 100%;\n" +
                "            border-collapse: collapse;\n" +
                "            margin-bottom: 15px;\n" +
                "        }\n" +
                "        .detail-table th {\n" +
                "            background-color: #f7f1e9;\n" +
                "            color: #7c5d3f;\n" +
                "            font-weight: bold;\n" +
                "            padding: 12px;\n" +
                "            font-size: 14px;\n" +
                "            text-align: left;\n" +
                "        }\n" +
                "        .detail-table th.text-right, .detail-table td.text-right {\n" +
                "            text-align: right;\n" +
                "        }\n" +
                "        .detail-table td {\n" +
                "            padding: 15px 12px;\n" +
                "            border-bottom: 1px solid #f0e6da;\n" +
                "            font-size: 14px;\n" +
                "            color: #4a4a4a;\n" +
                "        }\n" +
                "        .detail-table tr:last-child td {\n" +
                "            border-bottom: 2px solid #eaddcd;\n" +
                "        }\n" +
                "        .item-name {\n" +
                "            font-weight: 500;\n" +
                "        }\n" +
                "        .price-highlight {\n" +
                "            color: #bfa07a;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "        .summary-box {\n" +
                "            background-color: #fdfbf7;\n" +
                "            border: 1px solid #f0e6da;\n" +
                "            border-radius: 12px;\n" +
                "            padding: 20px;\n" +
                "            margin-bottom: 25px;\n" +
                "        }\n" +
                "        .summary-row {\n" +
                "            display: flex;\n" +
                "            justify-content: space-between;\n" +
                "            padding: 8px 0;\n" +
                "            font-size: 15px;\n" +
                "        }\n" +
                "        .summary-row.total-row {\n" +
                "            border-bottom: 1px solid #eaddcd;\n" +
                "            padding-bottom: 12px;\n" +
                "            margin-bottom: 12px;\n" +
                "            font-size: 16px;\n" +
                "        }\n" +
                "        .summary-label {\n" +
                "            color: #666666;\n" +
                "        }\n" +
                "        .summary-value {\n" +
                "            font-weight: bold;\n" +
                "            color: #333333;\n" +
                "        }\n" +
                "        .highlight-text {\n" +
                "            color: #bf9d7a;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            background-color: #f4eee6;\n" +
                "            padding: 20px;\n" +
                "            text-align: center;\n" +
                "            font-size: 13px;\n" +
                "            color: #8c7662;\n" +
                "        }\n" +
                "        .footer a {\n" +
                "            color: #bf9d7a;\n" +
                "            text-decoration: none;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "        .memo-box {\n" +
                "            background-color: #f9f9f9;\n" +
                "            border-left: 4px solid #dcdcdc;\n" +
                "            padding: 12px;\n" +
                "            font-size: 14px;\n" +
                "            color: #666;\n" +
                "            margin-top: 15px;\n" +
                "            border-radius: 0 8px 8px 0;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "    <div class=\"email-container\">\n" +
                "        <div class=\"header\">\n" +
                "            <div style=\"font-size: 28px; font-weight: bold; letter-spacing: 2px;\">毛起來</div>\n" +
                "            <div style=\"font-size: 12px; opacity: 0.9; margin-top: 5px;\">陪伴・毛孩・好好生活</div>\n" +
                "            <h1>預約成功通知</h1>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"content\">\n" +
                "            <div class=\"greeting\">親愛的毛家長：user_profiles.nickname，你好呀！</div>\n" +
                "            <p>我們已經收到您的 LINE Pay 訂金款項囉！感謝您對【毛起來】的信任與支持。看到預約成立，全體夥伴跟毛孩們一樣，高興得尾巴一直搖個不停呢！</p>\n" +
                "            <p>以下是您本次預約的明細與服務項目，請您過目確認：</p>\n" +
                "            \n" +
                "            <div class=\"status-badge\">LINE Pay 付款成功</div>\n" +
                "\n" +
                "            <div class=\"section-title\">預約單據資訊</div>\n" +
                "            <div class=\"info-card\">\n" +
                "                <div class=\"info-row\">\n" +
                "                    <span class=\"info-label\">訂單編號</span>\n" +
                "                    <span class=\"info-value\">ODR-20260629-3-002</span>\n" +
                "                </div>\n" +
                "                <div class=\"info-row\">\n" +
                "                    <span class=\"info-label\">付款單號</span>\n" +
                "                    <span class=\"info-value\">2026062902364691510</span>\n" +
                "                </div>\n" +
                "                <div class=\"info-row\">\n" +
                "                    <span class=\"info-label\">預約時間</span>\n" +
                "                    <span class=\"info-value\">2026-06-29 09:00</span>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "\n" +
                "            <div class=\"section-title\">預約服務項目明細</div>\n" +
                "            <table class=\"detail-table\">\n" +
                "                <thead>\n" +
                "                    <tr>\n" +
                "                        <th>服務項目</th>\n" +
                "                        <th class=\"text-right\" style=\"width: 80px;\">單價</th>\n" +
                "                        <th class=\"text-right\" style=\"width: 80px;\">小計</th>\n" +
                "                    </tr>\n" +
                "                </thead>\n" +
                "                <tbody>\n" +
                "                    <tr>\n" +
                "                        <td class=\"item-name\">基礎服務: 修剪腹部毛</td>\n" +
                "                        <td class=\"text-right\" style=\"color: #999;\">$ 300</td>\n" +
                "                        <td class=\"text-right price-highlight\">$ 300</td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td class=\"item-name\">基礎服務: 臉部小修剪</td>\n" +
                "                        <td class=\"text-right\" style=\"color: #999;\">$ 350</td>\n" +
                "                        <td class=\"text-right price-highlight\">$ 350</td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td class=\"item-name\">基礎服務: 深層梳理廢毛</td>\n" +
                "                        <td class=\"text-right\" style=\"color: #999;\">$ 400</td>\n" +
                "                        <td class=\"text-right price-highlight\">$ 400</td>\n" +
                "                    </tr>\n" +
                "                </tbody>\n" +
                "            </table>\n" +
                "\n" +
                "            <div class=\"summary-box\">\n" +
                "                <div class=\"summary-row total-row\">\n" +
                "                    <span class=\"summary-label\" style=\"color: #7c5d3f; font-weight: bold;\">服務項目總計</span>\n" +
                "                    <span class=\"summary-value\" style=\"font-size: 18px; color: #7c5d3f;\">NT$ 1,050</span>\n" +
                "                </div>\n" +
                "                <div class=\"summary-row\">\n" +
                "                    <span class=\"summary-label\">已付訂金金額</span>\n" +
                "                    <span class=\"summary-value highlight-text\">- NT$ 200</span>\n" +
                "                </div>\n" +
                "                <div class=\"summary-row\">\n" +
                "                    <span class=\"summary-label\">現場需付尾款</span>\n" +
                "                    <span class=\"summary-value\" style=\"color: #e06c55; font-size: 16px;\">NT$ 850</span>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "\n" +
                "            <div class=\"memo-box\">\n" +
                "                <b>預約備註：</b> 無備註事項\n" +
                "            </div>\n" +
                "\n" +
                "            <hr style=\"border: none; border-top: 1px solid #f0e6da; margin: 30px 0;\">\n" +
                "\n" +
                "            <p><b>溫馨小提醒：</b></p>\n" +
                "            <ul>\n" +
                "                <li>出發前別忘了幫毛孩裝進外出籠或繫好牽繩，安全第一唷！</li>\n" +
                "                <li>如果當天行程有變動，記得提前登入網站或聯絡我們改期，別讓我們和毛孩撲空囉～</li>\n" +
                "            </ul>\n" +
                "            \n" +
                "            <p style=\"margin-top: 30px; margin-bottom: 0;\">期待見面當天，給最寶貝的毛孩一場最讚的享受！</p>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"footer\">\n" +
                "            <p>此信件為系統自動發送，請勿直接回覆唷！</p>\n" +
                "            <p>有任何問題歡迎點擊 <a href=\"#\">聯絡線上客服</a> 或至 <a href=\"#\">會員中心</a> 查看詳情</p>\n" +
                "            <p style=\"margin-top: 15px; font-size: 11px; opacity: 0.8;\">© 2026 毛起來. All Rights Reserved.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
    }

    public WashLinePayTransactionBean getTransactionByOrderId(String orderId) {
        return transactionRepository.findByOrderId(orderId).orElse(null);
    }

    public String sign(String uri, String requestBody, String nonce) {
        try {
            String message = properties.getChannelSecret() + uri + requestBody + nonce;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(properties.getChannelSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmac);
        } catch (Exception e) {
            throw new IllegalStateException("LINE Pay signature failed", e);
        }
    }

    private JSONObject buildRequestBody(WashPaymentBean payment, String orderId) {
        int amount = payment.getAmount() != null ? payment.getAmount() : 0;
        String productName = buildProductName(payment);

        JSONObject product = new JSONObject()
                .put("name", productName)
                .put("quantity", 1)
                .put("price", amount);
        JSONObject pkg = new JSONObject()
                .put("id", orderId)
                .put("amount", amount)
                .put("products", new JSONArray().put(product));
        JSONObject redirectUrls = new JSONObject()
                .put("confirmUrl", properties.getConfirmUrl() + "?orderId=" + urlEncode(orderId))
                .put("cancelUrl", properties.getCancelUrl() + "?orderId=" + urlEncode(orderId));

        return new JSONObject()
                .put("amount", amount)
                .put("currency", properties.getCurrency())
                .put("orderId", orderId)
                .put("packages", new JSONArray().put(pkg))
                .put("redirectUrls", redirectUrls);
    }

    private JSONObject buildRequestResult(WashLinePayTransactionBean txn) {
        return new JSONObject()
                .put("linepayTxnId", txn.getLinepayTxnId())
                .put("orderId", txn.getOrderId())
                .put("transactionId", txn.getTransactionId() != null ? txn.getTransactionId() : JSONObject.NULL)
                .put("paymentUrl", txn.getPaymentUrl() != null ? txn.getPaymentUrl() : JSONObject.NULL);
    }

    private void saveCallback(WashLinePayTransactionBean txn, JSONObject response, String rawPayload, Byte callbackType, boolean verified) {
        WashLinePayCallbackBean callback = new WashLinePayCallbackBean();
        callback.setLinepayTxn(txn);
        callback.setOrderId(txn.getOrderId());
        callback.setTransactionId(txn.getTransactionId());
        callback.setReturnCode(response.optString("returnCode", null));
        callback.setReturnMessage(response.optString("returnMessage", null));
        callback.setRawPayload(rawPayload != null && rawPayload.length() > 500 ? rawPayload.substring(0, 500) : rawPayload);
        callback.setIsVerified(verified);
        callback.setCallbackType(callbackType);
        callback.setReceivedAt(LocalDateTime.now());
        callbackRepository.save(callback);
    }

    private String buildOrderId(WashPaymentBean payment) {
        return payment.getPayNo() != null ? payment.getPayNo() : "PAY-" + payment.getPaymentId();
    }

    private String buildProductName(WashPaymentBean payment) {
        if (payment.getPaymentPurpose() != null && payment.getPaymentPurpose() == 2) {
            return "PetHome wash deposit";
        }
        return "PetHome wash payment";
    }

    private boolean isDepositPayment(WashPaymentBean payment) {
        return payment.getPaymentPurpose() != null && payment.getPaymentPurpose() == 2;
    }

    private void autoConvertAppointmentToOrder(WashPaymentBean payment, LocalDateTime confirmedAt) {
        WashAppointmentBean appointment = payment.getApptOrder();
        if (appointment == null || appointment.getApptOrderId() == null) {
            return;
        }

        appointment.setApptStatus((byte) 3);
        appointment.setDepositStatus((byte) 1);
        appointment.setConfirmedAt(confirmedAt);
        appointment.setUpdatedAt(confirmedAt);
        appointment.setUpdatedBy("LINE_PAY");
        washAppointmentRepository.save(appointment);
    }

    private String buildOrderPayNo(WashPaymentBean payment) {
        WashAppointmentBean appointment = payment.getApptOrder();
        if (appointment != null && StringUtils.hasText(appointment.getApptNo())) {
            String apptNo = appointment.getApptNo();
            if (apptNo.startsWith("APT-")) {
                String orderNo = apptNo.replaceFirst("APT-", "ODR-");
                if (orderNo.equals(payment.getPayNo()) || !washPaymentRepository.existsByPayNo(orderNo)) {
                    return orderNo;
                }
            }
        }

        String dateStr = confirmedDatePart(payment.getPaidAt() != null ? payment.getPaidAt() : LocalDateTime.now());
        String prefix = "ODR-" + dateStr + "-1-";
        int sequence = (int) washPaymentRepository.countByPayNoStartingWith(prefix) + 1;
        String payNo = prefix + String.format("%03d", sequence);
        while (washPaymentRepository.existsByPayNo(payNo)) {
            sequence++;
            payNo = prefix + String.format("%03d", sequence);
        }
        return payNo;
    }

    private String confirmedDatePart(LocalDateTime value) {
        return String.format("%04d%02d%02d", value.getYear(), value.getMonthValue(), value.getDayOfMonth());
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @Transactional
    public JSONObject refundPayment(Long transactionId, int refundAmount) {
        validateConfig();

        String uri = "/v3/payments/" + transactionId + "/refund";
        String nonce = UUID.randomUUID().toString();
        JSONObject bodyJson = new JSONObject()
                .put("refundAmount", refundAmount);
        String body = bodyJson.toString();
        String signature = sign(uri, body, nonce);

        log.info("[LINE PAY REFUND] Requesting Refund. TransactionId: {}, Amount: {}, URI: {}",
                transactionId, refundAmount, uri);

        String responseBody = restClient.post()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("X-LINE-ChannelId", properties.getChannelId())
                .header("X-LINE-Authorization-Nonce", nonce)
                .header("X-LINE-Authorization", signature)
                .body(body)
                .retrieve()
                .body(String.class);

        log.info("[LINE PAY REFUND] Response received: {}", responseBody);

        JSONObject response = new JSONObject(responseBody);
        
        WashLinePayTransactionBean txn = transactionRepository.findByTransactionId(transactionId).orElse(null);

        WashLinePayRefundBean refund = new WashLinePayRefundBean();
        refund.setLinepayTxn(txn);
        refund.setRefundNo("REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        refund.setRefundAmount(refundAmount);
        refund.setNonce(nonce);
        refund.setSignature(signature);
        refund.setRequestUri(uri);
        
        boolean success = "0000".equals(response.optString("returnCode"));
        refund.setRefundStatus(success ? (byte) 1 : (byte) 2);
        refund.setReturnCode(response.optString("returnCode"));
        refund.setReturnMessage(response.optString("returnMessage"));
        
        JSONObject info = response.optJSONObject("info");
        if (info != null && info.has("refundTransactionId")) {
            refund.setRefundTransactionId(info.optLong("refundTransactionId"));
        }
        
        refund.setRequestedAt(LocalDateTime.now());
        if (success) {
            refund.setCompletedAt(LocalDateTime.now());
        }
        
        refundRepository.save(refund);

        if (!success) {
            throw new IllegalStateException(response.optString("returnMessage", "LINE Pay refund failed"));
        }

        return response;
    }

    private void validateConfig() {
        if (!StringUtils.hasText(properties.getApiBaseUrl())
                || !StringUtils.hasText(properties.getChannelId())
                || !StringUtils.hasText(properties.getChannelSecret())
                || !StringUtils.hasText(properties.getConfirmUrl())
                || !StringUtils.hasText(properties.getCancelUrl())) {
            throw new IllegalStateException("LINE Pay config is incomplete");
        }
    }
}
