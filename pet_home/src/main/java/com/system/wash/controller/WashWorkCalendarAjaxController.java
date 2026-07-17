package com.system.wash.controller;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.system.wash.entity.WashWorkCalendarBean;
import com.system.wash.entity.WashCalendarPeriodBean;
import com.system.wash.service.WashWorkCalendarService;
import com.system.wash.service.WashCalendarPeriodService;

@RestController
@CrossOrigin
public class WashWorkCalendarAjaxController {

    @Autowired
    private WashWorkCalendarService washWorkCalendarService;

    @Autowired
    private WashCalendarPeriodService washCalendarPeriodService;

    @PostMapping("/ajax/pages/WashWorkCalendars/find")
    public String find(@RequestBody String body) {
        JSONObject responseJson = new JSONObject();
        
        long count = washWorkCalendarService.count(body);
        responseJson.put("count", count);

        JSONArray array = new JSONArray();
        List<WashWorkCalendarBean> calendars = washWorkCalendarService.find(body);

        if (calendars != null && !calendars.isEmpty()) {
            for (WashWorkCalendarBean calendar : calendars) {
                array.put(convertToJson(calendar));
            }
        }

        responseJson.put("list", array);
        return responseJson.toString();
    }

    @GetMapping("/ajax/pages/WashWorkCalendars/{id}")
    public String findById(@PathVariable("id") Integer id) {
        JSONObject responseJson = new JSONObject();
        JSONArray array = new JSONArray();

        WashWorkCalendarBean calendar = washWorkCalendarService.findById(id);
        if (calendar != null) {
            array.put(convertToJson(calendar));
        }

        responseJson.put("list", array);
        return responseJson.toString();
    }

    @PostMapping("/ajax/pages/WashWorkCalendars")
    public String create(@RequestBody String body) {
        JSONObject responseJson = new JSONObject();
        
        WashWorkCalendarBean insert = washWorkCalendarService.create(body);

        if (insert == null) {
            responseJson.put("success", false);
            responseJson.put("message", "新增失敗");
        } else {
            responseJson.put("success", true);
            responseJson.put("message", "新增成功");
            responseJson.put("calendarId", insert.getCalendarId());
        }

        return responseJson.toString();
    }

    @PutMapping("/ajax/pages/WashWorkCalendars/{id}")
    public String modify(@PathVariable("id") Integer id, @RequestBody String body) {
        JSONObject responseJson = new JSONObject();

        if (id == null) {
            responseJson.put("success", false);
            responseJson.put("message", "CalendarId是必要欄位");
        } else if (!washWorkCalendarService.exists(id)) {
            responseJson.put("success", false);
            responseJson.put("message", "CalendarId不存在");
        } else {
            WashWorkCalendarBean update = washWorkCalendarService.modify(id, body);
            if (update == null) {
                responseJson.put("success", false);
                responseJson.put("message", "修改失敗");
            } else {
                responseJson.put("success", true);
                responseJson.put("message", "修改成功");
            }
        }

        return responseJson.toString();
    }

    @DeleteMapping("/ajax/pages/WashWorkCalendars/{id}")
    public String remove(@PathVariable("id") Integer id) {
        JSONObject responseJson = new JSONObject();

        if (id == null) {
            responseJson.put("success", false);
            responseJson.put("message", "CalendarId是必要欄位");
        } else if (!washWorkCalendarService.exists(id)) {
            responseJson.put("success", false);
            responseJson.put("message", "CalendarId不存在");
        } else {
            try {
                boolean result = washWorkCalendarService.remove(id);
                if (!result) {
                    responseJson.put("success", false);
                    responseJson.put("message", "刪除失敗");
                } else {
                    responseJson.put("success", true);
                    responseJson.put("message", "刪除成功");
                }
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                responseJson.put("success", false);
                responseJson.put("message", "無法刪除：該日期底下尚有相關聯的時段或排班資料");
            } catch (Exception e) {
                responseJson.put("success", false);
                responseJson.put("message", "刪除發生錯誤: " + e.getMessage());
            }
        }

        return responseJson.toString();
    }

    @GetMapping("/api/calendars")
    public String getAllCalendars() {
        List<WashWorkCalendarBean> calendars = washWorkCalendarService.findAllCalendars();
        
        JSONObject responseJson = new JSONObject();
        JSONArray array = new JSONArray();

        if (calendars != null && !calendars.isEmpty()) {
            for (WashWorkCalendarBean calendar : calendars) {
                array.put(convertToJson(calendar));
            }
        }
        
        responseJson.put("list", array);
        return responseJson.toString();
    }

    @GetMapping("/ajax/pages/WashWorkCalendars/all")
    public String findAll() {
        List<WashWorkCalendarBean> calendars = washWorkCalendarService.findAllCalendars();
        
        JSONObject responseJson = new JSONObject();
        JSONArray array = new JSONArray();

        if (calendars != null && !calendars.isEmpty()) {
            for (WashWorkCalendarBean calendar : calendars) {
                array.put(convertToJson(calendar));
            }
        }
        
        responseJson.put("list", array);
        return responseJson.toString();
    }

    @GetMapping("/api/calendars/{date}")
    public String getCalendarByDate(@PathVariable("date") String date) {
        JSONObject responseJson = new JSONObject();
        JSONArray array = new JSONArray();

        WashWorkCalendarBean calendar = washWorkCalendarService.findCalendarByDate(date);
        if (calendar != null) {
            array.put(convertToJson(calendar));
        }

        responseJson.put("list", array);
        return responseJson.toString();
    }

    private JSONObject convertToJson(WashWorkCalendarBean calendar) {
        JSONObject json = new JSONObject()
                .put("calendarId", calendar.getCalendarId())
                .put("workDate", calendar.getWorkDate() != null ? calendar.getWorkDate().toString() : null)
                .put("isWorkday", calendar.getIsWorkday())
                .put("dayType", calendar.getDayType())
                .put("note", calendar.getNote())
                .put("createdBy", calendar.getCreatedBy())
                .put("createdAt", calendar.getCreatedAt())
                .put("updatedAt", calendar.getUpdatedAt());

        // 將早午班的時段資訊一併附加進去回傳給前端
        JSONArray periodsArray = new JSONArray();
        List<WashCalendarPeriodBean> periods = washCalendarPeriodService.findByCalendarId(calendar.getCalendarId());
        if (periods != null && !periods.isEmpty()) {
            for (WashCalendarPeriodBean p : periods) {
                periodsArray.put(new JSONObject()
                        .put("periodOfDayId", p.getPeriodOfDayId())
                        .put("isActive", p.getIsActive()));
            }
        }
        json.put("periods", periodsArray);
        return json;
    }
}
