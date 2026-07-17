package com.system.wash.controller;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.system.wash.entity.WashApptDetailBean;
import com.system.wash.entity.WashServiceBean;
import com.system.wash.service.WashApptDetailService;
import com.system.wash.repository.WashPackageServiceItemRepository;

@RestController
@CrossOrigin
public class WashApptDetailAjaxController {

    @Autowired
    private WashApptDetailService washApptDetailService;

    @Autowired
    private WashPackageServiceItemRepository washPackageServiceItemRepository;

    @PostMapping("/ajax/pages/WashApptDetails/find")
    public String find(@RequestBody(required = false) String body) {
        if (body == null || body.trim().isEmpty()) {
            body = "{}";
        }
        System.out.println("查詢預約明細條件: " + body);
        JSONObject responseJson = new JSONObject();

        long count = washApptDetailService.count(body);
        responseJson.put("count", count);

        JSONArray array = new JSONArray();
        List<WashApptDetailBean> details = washApptDetailService.find(body);

        if (details != null && !details.isEmpty()) {
            for (WashApptDetailBean detail : details) {
                array.put(convertToJson(detail));
            }
        }

        responseJson.put("list", array);
        return responseJson.toString();
    }

    @GetMapping("/ajax/pages/WashApptDetails/{id}")
    public String findById(@PathVariable("id") Integer id) {
        JSONObject responseJson = new JSONObject();
        JSONArray array = new JSONArray();

        WashApptDetailBean detail = washApptDetailService.findById(id);
        if (detail != null) {
            array.put(convertToJson(detail));
        }

        responseJson.put("list", array);
        return responseJson.toString();
    }

    @PostMapping("/ajax/pages/WashApptDetails")
    public String create(@RequestBody(required = false) String body) {
        if (body == null || body.trim().isEmpty()) {
            body = "{}";
        }
        JSONObject responseJson = new JSONObject();

        WashApptDetailBean insert = washApptDetailService.create(body);

        if (insert == null) {
            responseJson.put("success", false);
            responseJson.put("message", "新增失敗");
        } else {
            responseJson.put("success", true);
            responseJson.put("message", "新增成功");
            responseJson.put("apptdetailId", insert.getApptdetailId());
        }

        return responseJson.toString();
    }

    @PutMapping("/ajax/pages/WashApptDetails/{id}")
    public String modify(@PathVariable("id") Integer id, @RequestBody(required = false) String body) {
        if (body == null || body.trim().isEmpty()) {
            body = "{}";
        }
        JSONObject responseJson = new JSONObject();

        if (id == null) {
            responseJson.put("success", false);
            responseJson.put("message", "ApptdetailId是必要欄位");
        } else if (!washApptDetailService.exists(id)) {
            responseJson.put("success", false);
            responseJson.put("message", "ApptdetailId不存在");
        } else {
            WashApptDetailBean update = washApptDetailService.modify(id, body);
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

    @DeleteMapping("/ajax/pages/WashApptDetails/{id}")
    public String remove(@PathVariable("id") Integer id) {
        JSONObject responseJson = new JSONObject();

        if (id == null || !washApptDetailService.exists(id)) {
            responseJson.put("success", false);
            responseJson.put("message", "ApptdetailId是必要欄位或不存在");
        } else {
            washApptDetailService.remove(id);
            responseJson.put("success", true);
            responseJson.put("message", "刪除成功");
        }

        return responseJson.toString();
    }

    private JSONObject convertToJson(WashApptDetailBean bean) {
        JSONObject json = new JSONObject()
                .put("apptdetailId", bean.getApptdetailId())
                .put("consumedPoints", bean.getConsumedPoints())
                .put("unitPrice", bean.getUnitPrice())
                .put("itemAmount", bean.getItemAmount());

        boolean isPackage = false;
        if (bean.getService() != null) {
            Integer serviceId = bean.getService().getServiceId();
            json.put("serviceId", serviceId);
            json.put("serviceName", bean.getService().getServiceName());

            // Check if it's a package service
            isPackage = washPackageServiceItemRepository.existsByServiceServiceIdAndTypeCode(serviceId, "PACKAGE");
            if (isPackage) {
                json.put("packageServiceItemId", serviceId); // Set packageServiceItemId to serviceId for frontend
                                                             // package compatibility
            }
        }

        if (bean.getPackageServiceItem() != null) {
            json.put("packageServiceItemId", bean.getPackageServiceItem().getPackageServiceItemId());
            if (bean.getPackageServiceItem().getService() != null) {
                json.put("serviceName", bean.getPackageServiceItem().getService().getServiceName());
            } else {
                json.put("serviceName", bean.getPackageServiceItem().getTypeName());
            }
            isPackage = true;
        }

        json.put("isPackage", isPackage);

        if (bean.getApptOrder() != null) {
            json.put("apptOrderId", bean.getApptOrder().getApptOrderId());
        }
        return json;
    }
}
