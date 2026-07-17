package com.system.wash.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.system.wash.repository.WashWorkCalendarDAO;
import com.system.wash.entity.WashWorkCalendarBean;
import com.system.wash.entity.WashCalendarPeriodBean;
import com.system.wash.repository.WashWorkCalendarRepository;
import com.system.wash.repository.WashCalendarPeriodRepository;
import com.system.wash.entity.WashScheduleBean;
import com.system.wash.repository.WashScheduleRepository;
import com.system.wash.entity.WashSlotTemplateBean;
import com.system.wash.repository.WashSlotTemplateRepository;

@Service
public class WashWorkCalendarService {

    @Autowired
    private WashWorkCalendarRepository washWorkCalendarRepository;

    @Autowired
    private WashWorkCalendarDAO washWorkCalendarDAO;

    @Autowired
    private WashCalendarPeriodRepository washCalendarPeriodRepository;

    @Autowired
    private WashScheduleRepository washScheduleRepository;

    @Autowired
    private WashSlotTemplateRepository washSlotTemplateRepository;

    public List<WashWorkCalendarBean> findAll() {
        return washWorkCalendarRepository.findAll();
    }

    public List<WashWorkCalendarBean> find(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            return washWorkCalendarDAO.find(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long count(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            return washWorkCalendarDAO.count(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public WashWorkCalendarBean findById(Integer calendarId) {
        if (calendarId != null) {
            return washWorkCalendarRepository.findById(calendarId).orElse(null);
        }
        return null;
    }

    public boolean exists(Integer calendarId) {
        return calendarId != null && washWorkCalendarRepository.existsById(calendarId);
    }

    @Transactional
    public WashWorkCalendarBean create(String json) {
        try {
            JSONObject obj = new JSONObject(json);

            Integer calendarId = obj.isNull("calendarId") ? null : obj.getInt("calendarId");

            if (calendarId == null || !washWorkCalendarRepository.existsById(calendarId)) {
                WashWorkCalendarBean insert = new WashWorkCalendarBean();
                insert.setCalendarId(calendarId);

                if (!obj.isNull("workDate")) {
                    insert.setWorkDate(LocalDate.parse(obj.getString("workDate")));
                } else {
                    // 防呆：如果沒有傳 workDate 就不進行新增，避免資料庫報錯
                    System.out.println("新增失敗：workDate 為必填欄位");
                    return null;
                }

                if (!obj.isNull("isWorkday")) {
                    insert.setIsWorkday(obj.getBoolean("isWorkday"));
                }
                
                if (!obj.isNull("dayType")) {
                    insert.setDayType((byte) obj.getInt("dayType"));
                }
                
                if (!obj.isNull("note")) {
                    insert.setNote(obj.getString("note"));
                }
                
                if (!obj.isNull("createdBy")) {
                    insert.setCreatedBy(obj.getString("createdBy"));
                }

                insert.setCreatedAt(LocalDateTime.now());
                insert.setUpdatedAt(LocalDateTime.now());

                WashWorkCalendarBean saved = washWorkCalendarRepository.save(insert);

                if (!obj.isNull("periods")) {
                    JSONArray periods = obj.getJSONArray("periods");
                    List<WashSlotTemplateBean> slotTemplates = washSlotTemplateRepository.findAll();
                    for (int i = 0; i < periods.length(); i++) {
                        JSONObject p = periods.getJSONObject(i);
                        int pId = p.getInt("periodOfDayId");
                        boolean isActive = p.getBoolean("isActive");

                        WashCalendarPeriodBean periodBean = new WashCalendarPeriodBean();
                        periodBean.setCalendarId(saved.getCalendarId());
                        periodBean.setPeriodOfDayId(pId);
                        periodBean.setIsActive(isActive);
                        washCalendarPeriodRepository.save(periodBean);

                        // --- 即時連動產生 WashSchedule (預約格子) ---
                        // 僅在 isWorkday 為 true 且 isActive 為 true 時才自動產格
                        if (isActive && Boolean.TRUE.equals(saved.getIsWorkday())) {
                            for (WashSlotTemplateBean template : slotTemplates) {
                                if (template.getPeriodOfDayId() != null && template.getPeriodOfDayId().equals(pId)) {
                                    WashScheduleBean schedule = new WashScheduleBean();
                                    schedule.setWorkDate(saved.getWorkDate());
                                    schedule.setPeriodOfDayId(pId);
                                    schedule.setSlotIndex(template.getSlotIndex());
                                    schedule.setSlotTemplateId((int) template.getSlotIndex());
                                    schedule.setIsAvailable(true);
                                    schedule.setIsAutoGenerated(true);
                                    schedule.setCreatedAt(LocalDateTime.now());
                                    schedule.setUpdatedAt(LocalDateTime.now());
                                    washScheduleRepository.save(schedule);
                                }
                            }
                        }
                    }
                }
                return saved;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public WashWorkCalendarBean modify(Integer id, String json) {
        try {
            JSONObject obj = new JSONObject(json);

            // 優先使用網址傳來的 id，如果沒有才找 JSON 內的 calendarId
            Integer calendarId = id != null ? id : (obj.isNull("calendarId") ? null : obj.getInt("calendarId"));

            if (calendarId != null) {
                WashWorkCalendarBean update = washWorkCalendarRepository.findById(calendarId).orElse(null);

                if (update != null) {
                    if (!obj.isNull("workDate")) {
                        update.setWorkDate(LocalDate.parse(obj.getString("workDate")));
                    }
                    if (!obj.isNull("isWorkday")) {
                        update.setIsWorkday(obj.getBoolean("isWorkday"));
                    }
                    if (!obj.isNull("dayType")) {
                        update.setDayType((byte) obj.getInt("dayType"));
                    }
                    if (!obj.isNull("note")) {
                        update.setNote(obj.getString("note"));
                    }
                    if (!obj.isNull("createdBy")) {
                        update.setCreatedBy(obj.getString("createdBy"));
                    }
                    
                    update.setUpdatedAt(LocalDateTime.now());

                    WashWorkCalendarBean saved = washWorkCalendarRepository.save(update);

                    List<WashCalendarPeriodBean> existingPeriods = washCalendarPeriodRepository.findByCalendarId(calendarId);
                    List<WashScheduleBean> existingSchedules = washScheduleRepository.findByWorkDate(saved.getWorkDate());
                    List<WashSlotTemplateBean> slotTemplates = washSlotTemplateRepository.findAll();
                    boolean isWorkday = Boolean.TRUE.equals(saved.getIsWorkday());

                    if (!obj.isNull("periods")) {
                        JSONArray periods = obj.getJSONArray("periods");
                        for (int i = 0; i < periods.length(); i++) {
                            JSONObject p = periods.getJSONObject(i);
                            int pId = p.getInt("periodOfDayId");
                            boolean isActive = p.getBoolean("isActive") && isWorkday;

                            WashCalendarPeriodBean matched = existingPeriods.stream()
                                    .filter(ep -> ep.getPeriodOfDayId().equals(pId))
                                    .findFirst()
                                    .orElse(new WashCalendarPeriodBean());
                                    
                            matched.setCalendarId(calendarId);
                            matched.setPeriodOfDayId(pId);
                            matched.setIsActive(isActive);
                            washCalendarPeriodRepository.save(matched);

                            // --- 即時連動修改 WashSchedule (預約格子) ---
                            List<WashSlotTemplateBean> periodTemplates = slotTemplates.stream()
                                    .filter(t -> t.getPeriodOfDayId() != null && t.getPeriodOfDayId().equals(pId))
                                    .toList();

                            for (WashSlotTemplateBean temp : periodTemplates) {
                                Byte sIndex = temp.getSlotIndex();
                                List<WashScheduleBean> matchedSchedules = existingSchedules.stream()
                                        .filter(s -> s.getSlotIndex() != null && s.getSlotIndex().equals(sIndex))
                                        .toList();

                                if (isActive) {
                                    if (matchedSchedules.isEmpty()) {
                                        WashScheduleBean schedule = new WashScheduleBean();
                                        schedule.setWorkDate(saved.getWorkDate());
                                        schedule.setPeriodOfDayId(pId);
                                        schedule.setSlotIndex(sIndex);
                                        schedule.setSlotTemplateId((int) sIndex);
                                        schedule.setIsAvailable(true);
                                        schedule.setIsAutoGenerated(true);
                                        schedule.setCreatedAt(LocalDateTime.now());
                                        schedule.setUpdatedAt(LocalDateTime.now());
                                        washScheduleRepository.save(schedule);
                                    } else {
                                        for (WashScheduleBean ms : matchedSchedules) {
                                            if (Boolean.FALSE.equals(ms.getIsAvailable()) && (ms.getApptOrderId() == null)) {
                                                ms.setIsAvailable(true);
                                                ms.setUpdatedAt(LocalDateTime.now());
                                                washScheduleRepository.save(ms);
                                            }
                                        }
                                    }
                                } else {
                                    for (WashScheduleBean ms : matchedSchedules) {
                                        if (ms.getApptOrderId() != null) {
                                            ms.setIsAvailable(false);
                                            ms.setUpdatedAt(LocalDateTime.now());
                                            washScheduleRepository.save(ms);
                                        } else {
                                            washScheduleRepository.delete(ms);
                                        }
                                    }
                                }
                            }
                        }
                    } else if (!isWorkday) {
                        // 如果沒有傳入 periods 欄位但工作日設為 false，則刪除或停用當天所有時段
                        for (WashScheduleBean ms : existingSchedules) {
                            if (ms.getApptOrderId() != null) {
                                ms.setIsAvailable(false);
                                ms.setUpdatedAt(LocalDateTime.now());
                                washScheduleRepository.save(ms);
                            } else {
                                washScheduleRepository.delete(ms);
                            }
                        }
                    }
                    return saved;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public boolean remove(Integer calendarId) {
        try {
            if (calendarId != null && washWorkCalendarRepository.existsById(calendarId)) {
                WashWorkCalendarBean calendar = washWorkCalendarRepository.findById(calendarId).orElse(null);
                
                // 連動刪除 Schedule
                if (calendar != null) {
                    List<WashScheduleBean> schedules = washScheduleRepository.findByWorkDate(calendar.getWorkDate());
                    for (WashScheduleBean schedule : schedules) {
                        if (schedule.getApptOrderId() == null) {
                            washScheduleRepository.delete(schedule);
                        } else {
                            // 若已有顧客預約，拋出例外觸發 Rollback，這段錯誤訊息會被 Controller 捕捉並回傳給前端
                            throw new RuntimeException("該日期已有顧客預約，無法刪除日曆！");
                        }
                    }
                }

                //先刪除關聯的子資料 (時段)
                washCalendarPeriodRepository.deleteByCalendarId(calendarId);
                //再刪除主資料 (日曆)
                washWorkCalendarRepository.deleteById(calendarId);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<WashWorkCalendarBean> findAllCalendars() {
        return washWorkCalendarRepository.findAll();
    }

    public WashWorkCalendarBean findCalendarByDate(String dateStr) {
        if (dateStr != null) {
            try {
                LocalDate workDate = LocalDate.parse(dateStr);
                return washWorkCalendarRepository.findByWorkDate(workDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
