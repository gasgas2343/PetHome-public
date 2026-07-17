package com.system.wash.controller;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.system.wash.entity.WashAppointmentBean;
import com.system.wash.service.WashAppointmentService;

@RestController
@CrossOrigin
public class WashAppointmentAjaxController {

    @Autowired
    private WashAppointmentService washAppointmentService;

    @PostMapping("/ajax/pages/WashAppointments/find")
    public String find(@RequestBody(required = false) String body) {
        if (body == null || body.trim().isEmpty()) {
            body = "{}";
        }
        System.out.println("查詢預約單條件: " + body);
        JSONObject responseJson = new JSONObject();

        long count = washAppointmentService.count(body);
        responseJson.put("count", count);

        JSONArray array = new JSONArray();
        List<WashAppointmentBean> appointments = washAppointmentService.find(body);

        if (appointments != null && !appointments.isEmpty()) {
            for (WashAppointmentBean appointment : appointments) {
                array.put(convertToJson(appointment));
            }
        }

        responseJson.put("list", array);

        // 計算各個狀態的全部預約筆數
        Long memberId = null;
        try {
            JSONObject obj = new JSONObject(body);
            if (!obj.isNull("memberId")) {
                memberId = obj.getLong("memberId");
            }
        } catch (Exception e) {
        }

        JSONObject statusCounts = new JSONObject();
        JSONObject countQueryAll = new JSONObject();
        if (memberId != null) {
            countQueryAll.put("memberId", memberId);
        }
        statusCounts.put("all", washAppointmentService.count(countQueryAll.toString()));

        for (int status : new int[] { 0, 1, 3, 4, 5 }) {
            JSONObject countQuery = new JSONObject();
            if (memberId != null) {
                countQuery.put("memberId", memberId);
            }
            countQuery.put("apptStatus", status);
            statusCounts.put(String.valueOf(status), washAppointmentService.count(countQuery.toString()));
        }
        responseJson.put("statusCounts", statusCounts);

        return responseJson.toString();
    }

    @GetMapping("/ajax/pages/WashAppointments/{id}")
    public String findById(@PathVariable("id") Integer id) {
        JSONObject responseJson = new JSONObject();
        JSONArray array = new JSONArray();

        WashAppointmentBean appointment = washAppointmentService.findById(id);
        if (appointment != null) {
            array.put(convertToJson(appointment));
        }

        responseJson.put("list", array);
        return responseJson.toString();
    }

    @PostMapping("/ajax/pages/WashAppointments")
    public String create(@RequestBody(required = false) String body) {
        if (body == null || body.trim().isEmpty()) {
            body = "{}";
        }
        JSONObject responseJson = new JSONObject();

        WashAppointmentBean insert = washAppointmentService.create(body);

        if (insert == null) {
            responseJson.put("success", false);
            responseJson.put("message", "新增失敗");
        } else {
            responseJson.put("success", true);
            responseJson.put("message", "新增成功");
            responseJson.put("apptOrderId", insert.getApptOrderId());
            responseJson.put("apptNo", insert.getApptNo());
        }

        return responseJson.toString();
    }

    @PutMapping("/ajax/pages/WashAppointments/{id}")
    public String modify(@PathVariable("id") Integer id, @RequestBody(required = false) String body) {
        if (body == null || body.trim().isEmpty()) {
            body = "{}";
        }
        JSONObject responseJson = new JSONObject();

        if (id == null) {
            responseJson.put("success", false);
            responseJson.put("message", "ApptOrderId是必要欄位");
        } else if (!washAppointmentService.exists(id)) {
            responseJson.put("success", false);
            responseJson.put("message", "ApptOrderId不存在");
        } else {
            WashAppointmentBean update = washAppointmentService.modify(id, body);
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

    @DeleteMapping("/ajax/pages/WashAppointments/{id}")
    public String remove(@PathVariable("id") Integer id) {
        JSONObject responseJson = new JSONObject();

        if (id == null) {
            responseJson.put("success", false);
            responseJson.put("message", "ApptOrderId是必要欄位");
        } else if (!washAppointmentService.exists(id)) {
            responseJson.put("success", false);
            responseJson.put("message", "ApptOrderId不存在");
        } else {
            boolean result = washAppointmentService.remove(id);
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

    private JSONObject convertToJson(WashAppointmentBean bean) {
        JSONObject json = new JSONObject()
                .put("apptOrderId", bean.getApptOrderId())
                .put("petId", bean.getPetId())
                .put("memberId", bean.getMemberId())
                .put("apptNo", bean.getApptNo())
                .put("sourceType", bean.getSourceType())
                .put("apptStatus", bean.getApptStatus())
                .put("confirmedAt", bean.getConfirmedAt() != null ? bean.getConfirmedAt().toString() : JSONObject.NULL)
                .put("cancelReason", bean.getCancelReason())
                .put("canceledBy", bean.getCanceledBy())
                .put("canceledAt", bean.getCanceledAt() != null ? bean.getCanceledAt().toString() : JSONObject.NULL)
                .put("note", bean.getNote())
                .put("apptDate", bean.getApptDate() != null ? bean.getApptDate().toString() : JSONObject.NULL)
                .put("apptStartTime",
                        bean.getApptStartTime() != null ? bean.getApptStartTime().toString() : JSONObject.NULL)
                .put("depositAmount", bean.getDepositAmount())
                .put("depositStatus", bean.getDepositStatus())
                .put("depositDeadline",
                        bean.getDepositDeadline() != null ? bean.getDepositDeadline().toString() : JSONObject.NULL)
                .put("subtotalAmount", bean.getSubtotalAmount())
                .put("discountAmount", bean.getDiscountAmount())
                .put("totalAmount", bean.getTotalAmount())
                .put("createdAt", bean.getCreatedAt() != null ? bean.getCreatedAt().toString() : JSONObject.NULL)
                .put("updatedAt", bean.getUpdatedAt() != null ? bean.getUpdatedAt().toString() : JSONObject.NULL);
        return json;
    }
}
