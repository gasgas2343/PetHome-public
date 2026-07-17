package com.system.wash.controller;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.system.wash.entity.WashPeriodOfDayBean;
import com.system.wash.service.WashPeriodOfDayService;

@RestController
@CrossOrigin
public class WashPeriodOfDayAjaxController {

    @Autowired
    private WashPeriodOfDayService washPeriodOfDayService;

    @PostMapping("/ajax/pages/WashPeriodOfDays/find")
    public String find(@RequestBody(required = false) String body) {
        if (body == null || body.trim().isEmpty()) {
            body = "{}";
        }
        JSONObject responseJson = new JSONObject();
        
        long count = washPeriodOfDayService.count(body);
        responseJson.put("count", count);

        JSONArray array = new JSONArray();
        List<WashPeriodOfDayBean> periods = washPeriodOfDayService.find(body);

        if (periods != null && !periods.isEmpty()) {
            for (WashPeriodOfDayBean period : periods) {
                array.put(convertToJson(period));
            }
        }

        responseJson.put("list", array);
        return responseJson.toString();
    }

    @GetMapping("/ajax/pages/WashPeriodOfDays/{id}")
    public String findById(@PathVariable("id") Integer id) {
        JSONObject responseJson = new JSONObject();
        JSONArray array = new JSONArray();

        WashPeriodOfDayBean period = washPeriodOfDayService.findById(id);
        if (period != null) {
            array.put(convertToJson(period));
        }

        responseJson.put("list", array);
        return responseJson.toString();
    }

    @PostMapping("/ajax/pages/WashPeriodOfDays")
    public String create(@RequestBody(required = false) String body) {
        if (body == null || body.trim().isEmpty()) {
            body = "{}";
        }
        JSONObject responseJson = new JSONObject();
        
        WashPeriodOfDayBean insert = washPeriodOfDayService.create(body);

        if (insert == null) {
            responseJson.put("success", false);
            responseJson.put("message", "新增失敗");
        } else {
            responseJson.put("success", true);
            responseJson.put("message", "新增成功");
            responseJson.put("periodOfDayId", insert.getPeriodOfDayId());
        }

        return responseJson.toString();
    }

    @PutMapping("/ajax/pages/WashPeriodOfDays/{id}")
    public String modify(@PathVariable("id") Integer id, @RequestBody(required = false) String body) {
        if (body == null || body.trim().isEmpty()) {
            body = "{}";
        }
        JSONObject responseJson = new JSONObject();

        if (id == null) {
            responseJson.put("success", false);
            responseJson.put("message", "PeriodOfDayId是必要欄位");
        } else if (!washPeriodOfDayService.exists(id)) {
            responseJson.put("success", false);
            responseJson.put("message", "PeriodOfDayId不存在");
        } else {
            WashPeriodOfDayBean update = washPeriodOfDayService.modify(id, body);
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

    @DeleteMapping("/ajax/pages/WashPeriodOfDays/{id}")
    public String remove(@PathVariable("id") Integer id) {
        JSONObject responseJson = new JSONObject();

        if (id == null) {
            responseJson.put("success", false);
            responseJson.put("message", "PeriodOfDayId是必要欄位");
        } else if (!washPeriodOfDayService.exists(id)) {
            responseJson.put("success", false);
            responseJson.put("message", "PeriodOfDayId不存在");
        } else {
            boolean result = washPeriodOfDayService.remove(id);
            if (!result) {
                responseJson.put("success", false);
                responseJson.put("message", "刪除失敗");
            } else {
                responseJson.put("success", true);
                responseJson.put("message", "刪除成功");
            }
        }

        return responseJson.toString();
    }

    @GetMapping("/api/periodOfDays")
    public String getAllPeriodOfDays() {
        List<WashPeriodOfDayBean> periods = washPeriodOfDayService.findAllPeriodOfDays();
        
        JSONObject responseJson = new JSONObject();
        JSONArray array = new JSONArray();

        if (periods != null && !periods.isEmpty()) {
            for (WashPeriodOfDayBean period : periods) {
                array.put(convertToJson(period));
            }
        }
        
        responseJson.put("list", array);
        return responseJson.toString();
    }

    @GetMapping("/ajax/pages/WashPeriodOfDays/all")
    public String findAll() {
        return getAllPeriodOfDays();
    }

    private JSONObject convertToJson(WashPeriodOfDayBean period) {
        return new JSONObject()
                .put("periodOfDayId", period.getPeriodOfDayId())
                .put("periodName", period.getPeriodName())
                // 將 java.sql.Time (或其他時間型態) 轉為 String，避免 NullPointerException
                .put("defaultStart", period.getDefaultStart() != null ? period.getDefaultStart().toString() : null)
                .put("defaultEnd", period.getDefaultEnd() != null ? period.getDefaultEnd().toString() : null)
                .put("isActive", period.getIsActive());
    }
}
