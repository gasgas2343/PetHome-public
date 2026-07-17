package com.system.wash.controller;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.system.wash.entity.WashPaymentBean;
import com.system.wash.service.WashPaymentService;

@RestController
@CrossOrigin
public class WashPaymentAjaxController {

    @Autowired
    private WashPaymentService washPaymentService;

    @Autowired
    private com.system.wash.repository.WashPaymentRepository washPaymentRepository;

    @PostMapping("/ajax/pages/WashPayments/find")
    public String find(@RequestBody(required = false) String body) {
        if (body == null || body.trim().isEmpty()) {
            body = "{}";
        }
        System.out.println("查詢付款單條件: " + body);
        JSONObject responseJson = new JSONObject();

        long count = washPaymentService.count(body);
        responseJson.put("count", count);

        JSONArray array = new JSONArray();
        List<WashPaymentBean> payments = washPaymentService.find(body);

        if (payments != null && !payments.isEmpty()) {
            for (WashPaymentBean payment : payments) {
                array.put(convertToJson(payment));
            }
        }

        responseJson.put("list", array);
        return responseJson.toString();
    }

    @GetMapping("/ajax/pages/WashPayments/{id}")
    public String findById(@PathVariable("id") Integer id) {
        JSONObject responseJson = new JSONObject();
        JSONArray array = new JSONArray();

        WashPaymentBean payment = washPaymentService.findById(id);
        if (payment != null) {
            array.put(convertToJson(payment));
        }

        responseJson.put("list", array);
        return responseJson.toString();
    }

    @PostMapping("/ajax/pages/WashPayments")
    public String create(@RequestBody(required = false) String body) {
        if (body == null || body.trim().isEmpty()) {
            body = "{}";
        }
        JSONObject responseJson = new JSONObject();

        WashPaymentBean insert = washPaymentService.create(body);

        if (insert == null) {
            responseJson.put("success", false);
            responseJson.put("message", "新增付款失敗");
        } else {
            responseJson.put("success", true);
            responseJson.put("message", "新增付款成功");
            responseJson.put("paymentId", insert.getPaymentId());
            responseJson.put("payNo", insert.getPayNo());
        }

        return responseJson.toString();
    }

    @PutMapping("/ajax/pages/WashPayments/{id}")
    public String modify(@PathVariable("id") Integer id, @RequestBody(required = false) String body) {
        if (body == null || body.trim().isEmpty()) {
            body = "{}";
        }
        JSONObject responseJson = new JSONObject();

        if (id == null) {
            responseJson.put("success", false);
            responseJson.put("message", "paymentId是必要欄位");
        } else if (!washPaymentService.exists(id)) {
            responseJson.put("success", false);
            responseJson.put("message", "paymentId不存在");
        } else {
            WashPaymentBean update = washPaymentService.modify(id, body);
            if (update == null) {
                responseJson.put("success", false);
                responseJson.put("message", "修改失敗");
            } else {
                responseJson.put("success", true);
                responseJson.put("message", "修改成功");
                responseJson.put("paymentId", update.getPaymentId());
                responseJson.put("payNo", update.getPayNo());
            }
        }

        return responseJson.toString();
    }

    @DeleteMapping("/ajax/pages/WashPayments/{id}")
    public String remove(@PathVariable("id") Integer id) {
        JSONObject responseJson = new JSONObject();
        if (id == null) {
            responseJson.put("success", false);
            responseJson.put("message", "paymentId是必要欄位");
        } else if (!washPaymentService.exists(id)) {
            responseJson.put("success", false);
            responseJson.put("message", "paymentId不存在");
        } else {
            boolean delete = washPaymentService.remove(id);
            if (!delete) {
                responseJson.put("success", false);
                responseJson.put("message", "刪除失敗");
            } else {
                responseJson.put("success", true);
                responseJson.put("message", "刪除成功");
            }
        }
        return responseJson.toString();
    }

    private JSONObject convertToJson(WashPaymentBean bean) {
        JSONObject obj = new JSONObject();
        if (bean != null) {
            obj.put("paymentId", bean.getPaymentId());
            obj.put("payment_id", bean.getPaymentId());

            if (bean.getApptOrder() != null) {
                obj.put("apptOrderId", bean.getApptOrder().getApptOrderId());
                obj.put("appt_order_id", bean.getApptOrder().getApptOrderId());
                obj.put("apptNo", bean.getApptOrder().getApptNo());
                obj.put("appt_no", bean.getApptOrder().getApptNo());

                String origOrderNo = "";
                List<WashPaymentBean> siblingPayments = washPaymentRepository
                        .findByApptOrderApptOrderId(bean.getApptOrder().getApptOrderId());
                if (siblingPayments != null) {
                    for (WashPaymentBean p : siblingPayments) {
                        if (p.getPayNo() != null && p.getPayNo().startsWith("ODR-")) {
                            origOrderNo = p.getPayNo();
                            break;
                        }
                    }
                }
                obj.put("origOrderNo", origOrderNo.isEmpty() ? JSONObject.NULL : origOrderNo);
                obj.put("orig_order_no", origOrderNo.isEmpty() ? JSONObject.NULL : origOrderNo);

                obj.put("apptTotalAmount",
                        bean.getApptOrder().getTotalAmount() != null ? bean.getApptOrder().getTotalAmount()
                                : JSONObject.NULL);
                obj.put("appt_total_amount",
                        bean.getApptOrder().getTotalAmount() != null ? bean.getApptOrder().getTotalAmount()
                                : JSONObject.NULL);
                obj.put("apptDepositAmount",
                        bean.getApptOrder().getDepositAmount() != null ? bean.getApptOrder().getDepositAmount()
                                : JSONObject.NULL);
                obj.put("appt_deposit_amount",
                        bean.getApptOrder().getDepositAmount() != null ? bean.getApptOrder().getDepositAmount()
                                : JSONObject.NULL);
            } else {
                obj.put("apptOrderId", JSONObject.NULL);
                obj.put("appt_order_id", JSONObject.NULL);
                obj.put("apptNo", JSONObject.NULL);
                obj.put("appt_no", JSONObject.NULL);
                obj.put("origOrderNo", JSONObject.NULL);
                obj.put("orig_order_no", JSONObject.NULL);
                obj.put("apptTotalAmount", JSONObject.NULL);
                obj.put("appt_total_amount", JSONObject.NULL);
                obj.put("apptDepositAmount", JSONObject.NULL);
                obj.put("appt_deposit_amount", JSONObject.NULL);
            }

            obj.put("memberPointId", bean.getMemberPointId() != null ? bean.getMemberPointId() : JSONObject.NULL);
            obj.put("member_point_id", bean.getMemberPointId() != null ? bean.getMemberPointId() : JSONObject.NULL);

            obj.put("memberId", bean.getMemberId() != null ? bean.getMemberId() : JSONObject.NULL);
            obj.put("member_id", bean.getMemberId() != null ? bean.getMemberId() : JSONObject.NULL);

            if (bean.getSavedCard() != null) {
                obj.put("savedCardId", bean.getSavedCard().getSavedCardId());
                obj.put("saved_card_id", bean.getSavedCard().getSavedCardId());
            } else {
                obj.put("savedCardId", JSONObject.NULL);
                obj.put("saved_card_id", JSONObject.NULL);
            }

            obj.put("payNo", bean.getPayNo());
            obj.put("pay_no", bean.getPayNo());

            obj.put("paymentPurpose",
                    bean.getPaymentPurpose() != null ? bean.getPaymentPurpose().intValue() : JSONObject.NULL);
            obj.put("payment_purpose",
                    bean.getPaymentPurpose() != null ? bean.getPaymentPurpose().intValue() : JSONObject.NULL);

            obj.put("transactionType",
                    bean.getTransactionType() != null ? bean.getTransactionType().intValue() : JSONObject.NULL);
            obj.put("transaction_type",
                    bean.getTransactionType() != null ? bean.getTransactionType().intValue() : JSONObject.NULL);

            obj.put("amount", bean.getAmount() != null ? bean.getAmount() : JSONObject.NULL);
            obj.put("paymentMethod",
                    bean.getPaymentMethod() != null ? bean.getPaymentMethod().intValue() : JSONObject.NULL);
            obj.put("payment_method",
                    bean.getPaymentMethod() != null ? bean.getPaymentMethod().intValue() : JSONObject.NULL);

            obj.put("pointTopupAmount",
                    bean.getPointTopupAmount() != null ? bean.getPointTopupAmount() : JSONObject.NULL);
            obj.put("point_topup_amount",
                    bean.getPointTopupAmount() != null ? bean.getPointTopupAmount() : JSONObject.NULL);

            obj.put("pointTopupBonus", bean.getPointTopupBonus() != null ? bean.getPointTopupBonus() : JSONObject.NULL);
            obj.put("point_topup_bonus",
                    bean.getPointTopupBonus() != null ? bean.getPointTopupBonus() : JSONObject.NULL);

            obj.put("transactionNo", bean.getTransactionNo() != null ? bean.getTransactionNo() : JSONObject.NULL);
            obj.put("transaction_no", bean.getTransactionNo() != null ? bean.getTransactionNo() : JSONObject.NULL);

            obj.put("paidAt", bean.getPaidAt() != null ? bean.getPaidAt().toString() : JSONObject.NULL);
            obj.put("paid_at", bean.getPaidAt() != null ? bean.getPaidAt().toString() : JSONObject.NULL);

            obj.put("paymentStatus",
                    bean.getPaymentStatus() != null ? bean.getPaymentStatus().intValue() : JSONObject.NULL);
            obj.put("payment_status",
                    bean.getPaymentStatus() != null ? bean.getPaymentStatus().intValue() : JSONObject.NULL);

            obj.put("createdAt", bean.getCreatedAt() != null ? bean.getCreatedAt().toString() : JSONObject.NULL);
            obj.put("created_at", bean.getCreatedAt() != null ? bean.getCreatedAt().toString() : JSONObject.NULL);
        }
        return obj;
    }
}
