package com.system.wash.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.system.wash.entity.WashAppointmentBean;
import com.system.wash.repository.WashAppointmentDAO;
import com.system.wash.repository.WashAppointmentRepository;
import com.system.wash.repository.WashScheduleRepository;

@Service
public class WashAppointmentService {

    @Autowired
    private WashAppointmentRepository washAppointmentRepository;

    @Autowired
    private WashAppointmentDAO washAppointmentDAO;

    @Autowired
    private WashScheduleRepository washScheduleRepository;

    @Transactional
    public void checkAndCancelExpiredAppointments() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<WashAppointmentBean> expiredList = washAppointmentRepository.findExpiredAppointments(now);
            if (expiredList != null && !expiredList.isEmpty()) {
                for (WashAppointmentBean appt : expiredList) {
                    appt.setApptStatus((byte) 4); // 4: 已取消
                    appt.setCanceledBy("系統自動");
                    appt.setCanceledAt(now);
                    appt.setCancelReason("繳納訂金期限已過期");
                    appt.setUpdatedAt(now);
                    washAppointmentRepository.save(appt);
                    
                    // Also free up the reserved slots in WashSchedule
                    List<com.system.wash.entity.WashScheduleBean> schedules = washScheduleRepository.findByApptOrderId(appt.getApptOrderId());
                    if (schedules != null && !schedules.isEmpty()) {
                        for (com.system.wash.entity.WashScheduleBean schedule : schedules) {
                            schedule.setIsAvailable(true);
                            schedule.setApptOrderId(null);
                            schedule.setUpdatedAt(now);
                            washScheduleRepository.save(schedule);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("自動取消過期訂金預約失敗: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<WashAppointmentBean> find(String json) {
        checkAndCancelExpiredAppointments();
        try {
            JSONObject obj = new JSONObject(json);
            return washAppointmentDAO.find(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long count(String json) {
        checkAndCancelExpiredAppointments();
        try {
            JSONObject obj = new JSONObject(json);
            return washAppointmentDAO.count(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public WashAppointmentBean findById(Integer apptOrderId) {
        checkAndCancelExpiredAppointments();
        if (apptOrderId != null) {
            return washAppointmentRepository.findById(apptOrderId).orElse(null);
        }
        return null;
    }

    public boolean exists(Integer apptOrderId) {
        return apptOrderId != null && washAppointmentRepository.existsById(apptOrderId);
    }

    public String generateApptNo(LocalDateTime apptDate) {
        return generateApptNo(apptDate, 1);
    }

    public String generateApptNo(LocalDateTime apptDate, int slotIndex) {
        if (apptDate == null) {
            apptDate = LocalDateTime.now();
        }
        String dateStr = apptDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "APT-" + dateStr + "-" + slotIndex + "-";
        long count = washAppointmentRepository.countByApptNoStartingWith(prefix);
        int sequence = (int) count + 1;
        String apptNo = prefix + String.format("%03d", sequence);
        while (washAppointmentRepository.existsByApptNo(apptNo)) {
            sequence++;
            apptNo = prefix + String.format("%03d", sequence);
        }
        return apptNo;
    }

    @Transactional
    public WashAppointmentBean create(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            WashAppointmentBean insert = new WashAppointmentBean();

            mapJsonToBean(obj, insert);

            int slotIndex = 0;
            if (obj.has("slotIndex") && !obj.isNull("slotIndex")) {
                slotIndex = obj.getInt("slotIndex");
            } else if (obj.has("slot_index") && !obj.isNull("slot_index")) {
                slotIndex = obj.getInt("slot_index");
            }
            if (slotIndex < 1 || slotIndex > 8) {
                if (insert.getApptStartTime() != null) {
                    int hour = insert.getApptStartTime().getHour();
                    if (hour == 9) slotIndex = 1;
                    else if (hour == 10) slotIndex = 2;
                    else if (hour == 11) slotIndex = 3;
                    else if (hour == 12) slotIndex = 4;
                    else if (hour == 14) slotIndex = 5;
                    else if (hour == 15) slotIndex = 6;
                    else if (hour == 16) slotIndex = 7;
                    else if (hour == 17) slotIndex = 8;
                }
            }
            if (slotIndex < 1 || slotIndex > 8) {
                slotIndex = 1;
            }

            if (insert.getApptNo() == null || insert.getApptNo().trim().isEmpty()
                    || insert.getApptNo().equalsIgnoreCase("null")
                    || washAppointmentRepository.existsByApptNo(insert.getApptNo())) {
                insert.setApptNo(generateApptNo(insert.getApptDate(), slotIndex));
            }

            insert.setCreatedAt(LocalDateTime.now());
            insert.setUpdatedAt(LocalDateTime.now());

            return washAppointmentRepository.save(insert);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public WashAppointmentBean modify(Integer id, String json) {
        try {
            JSONObject obj = new JSONObject(json);
            Integer apptOrderId = id != null ? id : (obj.isNull("apptOrderId") ? null : obj.getInt("apptOrderId"));

            if (apptOrderId != null) {
                WashAppointmentBean update = washAppointmentRepository.findById(apptOrderId).orElse(null);
                if (update != null) {
                    mapJsonToBean(obj, update);
                    update.setUpdatedAt(LocalDateTime.now());
                    
                    // If updated to status 4 (Canceled), automatically free up the reserved slots in WashSchedule!
                    if (update.getApptStatus() == (byte) 4) {
                        List<com.system.wash.entity.WashScheduleBean> schedules = washScheduleRepository.findByApptOrderId(update.getApptOrderId());
                        if (schedules != null && !schedules.isEmpty()) {
                            for (com.system.wash.entity.WashScheduleBean schedule : schedules) {
                                schedule.setIsAvailable(true);
                                schedule.setApptOrderId(null);
                                schedule.setUpdatedAt(LocalDateTime.now());
                                washScheduleRepository.save(schedule);
                            }
                        }
                    }
                    
                    return washAppointmentRepository.save(update);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public boolean remove(Integer apptOrderId) {
        try {
            if (apptOrderId != null && washAppointmentRepository.existsById(apptOrderId)) {
                washAppointmentRepository.deleteById(apptOrderId);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<WashAppointmentBean> findAll() {
        return washAppointmentRepository.findAll();
    }

    /**
     * 頛?寞?嚗? JSONObject ?抒?甈?????WashAppointmentBean
     */
    private void mapJsonToBean(JSONObject obj, WashAppointmentBean bean) {
        if (!obj.isNull("petId"))
            bean.setPetId(obj.getLong("petId"));
        if (!obj.isNull("memberId"))
            bean.setMemberId(obj.getLong("memberId"));
        if (!obj.isNull("apptNo"))
            bean.setApptNo(obj.getString("apptNo"));
        if (!obj.isNull("sourceType"))
            bean.setSourceType((byte) obj.getInt("sourceType"));
        if (!obj.isNull("apptStatus"))
            bean.setApptStatus((byte) obj.getInt("apptStatus"));
        if (!obj.isNull("cancelReason"))
            bean.setCancelReason(obj.getString("cancelReason"));
        if (!obj.isNull("canceledBy"))
            bean.setCanceledBy(obj.getString("canceledBy"));
        if (!obj.isNull("note"))
            bean.setNote(obj.getString("note"));
        if (!obj.isNull("depositAmount"))
            bean.setDepositAmount(obj.getInt("depositAmount"));
        if (!obj.isNull("depositStatus"))
            bean.setDepositStatus((byte) obj.getInt("depositStatus"));
        if (!obj.isNull("subtotalAmount"))
            bean.setSubtotalAmount(obj.getInt("subtotalAmount"));
        if (!obj.isNull("discountAmount"))
            bean.setDiscountAmount(obj.getInt("discountAmount"));
        if (!obj.isNull("totalAmount"))
            bean.setTotalAmount(obj.getInt("totalAmount"));
        if (!obj.isNull("updatedBy"))
            bean.setUpdatedBy(obj.getString("updatedBy"));

        // 處理時間格式字串 (視前端傳遞格式，若只有日期需處理補時間或直接剖析)
        bean.setConfirmedAt(parseDateTime(obj, "confirmedAt"));
        bean.setCanceledAt(parseDateTime(obj, "canceledAt"));
        bean.setApptDate(parseDateTime(obj, "apptDate"));
        bean.setApptStartTime(parseDateTime(obj, "apptStartTime"));
        bean.setDepositDeadline(parseDateTime(obj, "depositDeadline"));

        // 注意：外鍵關聯的實體 (petGroomer, lineNotify) 若前端有傳遞 ID，可以在此處透過 Repository 取得或設定關聯
        // 例如：
        // if (!obj.isNull("petGroomerId")) {
        // WashPetGroomerBean groomer = new WashPetGroomerBean();
        // groomer.setPetGroomerId(obj.getInt("petGroomerId"));
        // bean.setPetGroomer(groomer);
        // }
    }

    private LocalDateTime parseDateTime(JSONObject obj, String key) {
        if (obj.isNull(key))
            return null;
        String val = obj.getString(key);
        if (val == null || val.trim().isEmpty())
            return null;

        try {
            // 如果傳入格式包含 T，表示為標準 ISO-8601，例如 "2026-06-25T10:00:00"
            if (val.contains("T")) {
                return LocalDateTime.parse(val);
            }
            // 如果傳入格式為 yyyy-MM-dd HH:mm:ss
            else if (val.contains(":") && val.contains("-")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return LocalDateTime.parse(val, formatter);
            }
            // 如果只傳入日期 yyyy-MM-dd，預設給 00:00:00
            else if (val.length() == 10 && val.contains("-")) {
                val += "T00:00:00";
                return LocalDateTime.parse(val);
            }
            // 其它格式可在此擴充
            else {
                return LocalDateTime.parse(val);
            }
        } catch (Exception e) {
            System.out.println("時間解析失敗: " + key + " = " + val);
            e.printStackTrace();
        }
        return null;
    }
}
