package com.system.wash.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.system.wash.entity.WashPaymentBean;
import com.system.wash.entity.WashAppointmentBean;
import com.system.wash.entity.WashMemberSavedCardBean;
import com.system.wash.repository.WashPaymentRepository;
import com.system.wash.repository.WashPaymentDAO;
import com.system.wash.repository.WashAppointmentRepository;
import com.system.wash.repository.WashScheduleRepository;
import com.system.wash.entity.WashScheduleBean;

@Service
public class WashPaymentService {

    private static final Logger log = LoggerFactory.getLogger(WashPaymentService.class);

    @Autowired
    private WashPaymentRepository washPaymentRepository;

    @Autowired
    private WashPaymentDAO washPaymentDAO;

    @Autowired
    private WashLinePayService washLinePayService;

    @Autowired
    private WashAppointmentRepository washAppointmentRepository;

    @Autowired
    private WashScheduleRepository washScheduleRepository;

    public List<WashPaymentBean> find(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            return washPaymentDAO.find(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long count(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            return washPaymentDAO.count(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public WashPaymentBean findById(Integer id) {
        if (id != null) {
            return washPaymentRepository.findById(id).orElse(null);
        }
        return null;
    }

    public boolean exists(Integer id) {
        return id != null && washPaymentRepository.existsById(id);
    }

    @Transactional
    public WashPaymentBean create(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            WashPaymentBean insert = new WashPaymentBean();
            mapJsonToBean(obj, insert);
            insert.setCreatedAt(LocalDateTime.now());

            // Check if this is a LINE Pay refund request
            if (insert.getTransactionType() != null && insert.getTransactionType() == 1
                    && insert.getPaymentMethod() != null && insert.getPaymentMethod() == 3) {
                
                if (insert.getApptOrder() == null || insert.getApptOrder().getApptOrderId() == null) {
                    throw new IllegalArgumentException("apptOrderId is required for refund");
                }
                Integer apptOrderId = insert.getApptOrder().getApptOrderId();
                
                // Find the original payment transaction
                WashPaymentBean origPayment = washPaymentRepository
                        .findFirstByApptOrderApptOrderIdAndTransactionTypeAndPaymentStatusAndPaymentMethod(
                                apptOrderId, (byte) 0, (byte) 1, (byte) 3)
                        .orElseThrow(() -> new IllegalArgumentException("No successful LINE Pay payment found for apptOrderId: " + apptOrderId));
                
                String origTxnNo = origPayment.getTransactionNo();
                if (origTxnNo == null || origTxnNo.trim().isEmpty()) {
                    throw new IllegalArgumentException("Original transaction ID is missing");
                }
                
                Long transactionId = Long.parseLong(origTxnNo.trim());
                int refundAmount = insert.getAmount() != null ? insert.getAmount() : origPayment.getAmount();
                
                log.info("[REFUND PROCESS] Initiating LINE Pay refund. TransactionId: {}, Amount: {}", transactionId, refundAmount);
                
                // Call LINE Pay refund API
                JSONObject linePayResponse = washLinePayService.refundPayment(transactionId, refundAmount);
                
                // Update refund payment fields with success details
                insert.setPaymentStatus((byte) 1); // Success
                insert.setPaidAt(LocalDateTime.now());
                
                JSONObject info = linePayResponse.optJSONObject("info");
                if (info != null && info.has("refundTransactionId")) {
                    insert.setTransactionNo(String.valueOf(info.optLong("refundTransactionId")));
                } else {
                    insert.setTransactionNo(origTxnNo);
                }
                
                WashPaymentBean savedRefund = washPaymentRepository.save(insert);
                
                // Update corresponding WashAppointment status
                WashAppointmentBean appt = washAppointmentRepository.findById(apptOrderId).orElse(null);
                if (appt != null) {
                    appt.setDepositStatus((byte) 2); // 2: 已退款
                    appt.setApptStatus((byte) 4);    // 4: 已取消
                    appt.setUpdatedAt(LocalDateTime.now());
                    appt.setUpdatedBy("REFUND_API");
                    washAppointmentRepository.save(appt);
                }

                // Free up the reserved slots in WashSchedule so they can be booked again
                List<WashScheduleBean> schedules = washScheduleRepository.findByApptOrderId(apptOrderId);
                if (schedules != null && !schedules.isEmpty()) {
                    for (WashScheduleBean schedule : schedules) {
                        schedule.setIsAvailable(true);
                        schedule.setApptOrderId(null);
                        schedule.setUpdatedAt(LocalDateTime.now());
                        washScheduleRepository.save(schedule);
                        log.info("[REFUND PROCESS] Released schedule slot. ScheduleId: {}, WorkDate: {}, SlotIndex: {}",
                                schedule.getScheduleId(), schedule.getWorkDate(), schedule.getSlotIndex());
                    }
                }
                
                log.info("[REFUND PROCESS] LINE Pay refund completed successfully. Refund paymentId: {}", savedRefund.getPaymentId());
                return savedRefund;
            } else {
                return washPaymentRepository.save(insert);
            }
        } catch (Exception e) {
            log.error("Failed to create WashPayment: {}", json, e);
        }
        return null;
    }

    @Transactional
    public WashPaymentBean modify(Integer id, String json) {
        try {
            JSONObject obj = new JSONObject(json);
            Integer paymentId = id != null ? id : (obj.isNull("paymentId") ? null : obj.getInt("paymentId"));
            if (paymentId != null) {
                WashPaymentBean update = washPaymentRepository.findById(paymentId).orElse(null);
                if (update != null) {
                    mapJsonToBean(obj, update);
                    return washPaymentRepository.save(update);
                }
            }
        } catch (Exception e) {
            log.error("Failed to modify WashPayment: {}", json, e);
        }
        return null;
    }

    @Transactional
    public boolean remove(Integer id) {
        if (id != null && washPaymentRepository.existsById(id)) {
            washPaymentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void mapJsonToBean(JSONObject obj, WashPaymentBean bean) {
        if (!obj.isNull("apptOrderId")) {
            WashAppointmentBean appt = new WashAppointmentBean();
            appt.setApptOrderId(obj.getInt("apptOrderId"));
            bean.setApptOrder(appt);
        }
        if (!obj.isNull("savedCardId")) {
            WashMemberSavedCardBean card = new WashMemberSavedCardBean();
            card.setSavedCardId(obj.getInt("savedCardId"));
            bean.setSavedCard(card);
        }
        if (!obj.isNull("memberPointId")) bean.setMemberPointId(obj.getInt("memberPointId"));
        if (!obj.isNull("memberId")) bean.setMemberId(obj.getLong("memberId"));
        if (!obj.isNull("payNo")) bean.setPayNo(obj.getString("payNo"));
        if (!obj.isNull("paymentPurpose")) bean.setPaymentPurpose((byte) obj.getInt("paymentPurpose"));
        if (!obj.isNull("transactionType")) bean.setTransactionType((byte) obj.getInt("transactionType"));
        if (!obj.isNull("amount")) bean.setAmount(obj.getInt("amount"));
        if (!obj.isNull("paymentMethod")) bean.setPaymentMethod((byte) obj.getInt("paymentMethod"));
        if (!obj.isNull("pointTopupAmount")) bean.setPointTopupAmount(obj.getInt("pointTopupAmount"));
        if (!obj.isNull("pointTopupBonus")) bean.setPointTopupBonus(obj.getInt("pointTopupBonus"));
        if (!obj.isNull("transactionNo")) bean.setTransactionNo(obj.getString("transactionNo"));
        
        if (!obj.isNull("paidAt")) {
            try {
                String paidAtStr = obj.getString("paidAt");
                if (paidAtStr != null && !paidAtStr.isEmpty()) {
                    paidAtStr = paidAtStr.replace(" ", "T");
                    bean.setPaidAt(LocalDateTime.parse(paidAtStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                }
            } catch (Exception e) {
                bean.setPaidAt(LocalDateTime.now());
            }
        }
        if (!obj.isNull("paymentStatus")) bean.setPaymentStatus((byte) obj.getInt("paymentStatus"));
    }
}
