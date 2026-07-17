package com.system.wash.controller;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.system.wash.entity.WashPackageServiceItemBean;
import com.system.wash.service.WashPackageServiceItemService;

@RestController
@CrossOrigin
public class WashPackageServiceItemController {

    @Autowired
    private WashPackageServiceItemService washPackageServiceItemService;

    @PostMapping("/ajax/pages/WashPackageServiceItems/find")
    public String find(@RequestBody(required = false) String body) {
        JSONObject responseJson = new JSONObject();

        // 目前 Service 無實作分頁或條件查詢，暫以 findAll 替代
        List<WashPackageServiceItemBean> items = washPackageServiceItemService.findAll();
        long count = items != null ? items.size() : 0;
        responseJson.put("count", count);

        JSONArray array = new JSONArray();
        if (items != null && !items.isEmpty()) {
            for (WashPackageServiceItemBean item : items) {
                array.put(convertToJson(item));
            }
        }

        responseJson.put("list", array);
        return responseJson.toString();
    }

    @GetMapping("/ajax/pages/WashPackageServiceItems/{id}")
    public String findById(@PathVariable("id") Integer id) {
        JSONObject responseJson = new JSONObject();
        JSONArray array = new JSONArray();

        WashPackageServiceItemBean item = washPackageServiceItemService.findById(id);
        if (item != null) {
            array.put(convertToJson(item));
        }

        responseJson.put("list", array);
        return responseJson.toString();
    }

    @PostMapping("/ajax/pages/WashPackageServiceItems")
    public String create(@RequestBody WashPackageServiceItemBean bean) {
        JSONObject responseJson = new JSONObject();

        WashPackageServiceItemBean insert = washPackageServiceItemService.insert(bean);

        if (insert == null) {
            responseJson.put("success", false);
            responseJson.put("message", "新增失敗");
        } else {
            responseJson.put("success", true);
            responseJson.put("message", "新增成功");
            responseJson.put("packageServiceItemId", insert.getPackageServiceItemId());
        }

        return responseJson.toString();
    }

    @PostMapping("/ajax/pages/WashPackageServiceItems/batch")
    public String createBatch(@RequestBody List<WashPackageServiceItemBean> beans) {
        JSONObject responseJson = new JSONObject();

        if (beans == null || beans.size() != 5) {
            responseJson.put("success", false);
            responseJson.put("message", "新增失敗：必須為五組子項目");
            return responseJson.toString();
        }

        int totalMinutes = 0;
        for (WashPackageServiceItemBean bean : beans) {
            if (bean == null) {
                responseJson.put("success", false);
                responseJson.put("message", "新增失敗：子項目資料不可為空");
                return responseJson.toString();
            }
            if (bean.getService() == null || bean.getService().getServiceId() == null) {
                responseJson.put("success", false);
                responseJson.put("message", "新增失敗：所屬服務ID不可為空");
                return responseJson.toString();
            }
            if (!"PACKAGE".equals(bean.getTypeCode())) {
                responseJson.put("success", false);
                responseJson.put("message", "新增失敗：非套裝服務類型");
                return responseJson.toString();
            }
            if (bean.getPeriodMinutes() == null) {
                responseJson.put("success", false);
                responseJson.put("message", "新增失敗：時長不可為空");
                return responseJson.toString();
            }
            totalMinutes += bean.getPeriodMinutes();
        }

        if (totalMinutes != 60) {
            responseJson.put("success", false);
            responseJson.put("message", "新增失敗：時長需要加起來1小時");
            return responseJson.toString();
        }

        List<WashPackageServiceItemBean> insert = washPackageServiceItemService.insertBatch(beans);

        if (insert == null) {
            responseJson.put("success", false);
            responseJson.put("message", "新增失敗");
        } else {
            responseJson.put("success", true);
            responseJson.put("message", "新增成功");
        }

        return responseJson.toString();
    }


    @PutMapping("/ajax/pages/WashPackageServiceItems/{id}")
    public String modify(@PathVariable("id") Integer id, @RequestBody WashPackageServiceItemBean bean) {
        JSONObject responseJson = new JSONObject();

        if (id == null) {
            responseJson.put("success", false);
            responseJson.put("message", "PackageServiceItemId是必要欄位");
        } else if (washPackageServiceItemService.findById(id) == null) {
            responseJson.put("success", false);
            responseJson.put("message", "PackageServiceItemId不存在");
        } else {
            bean.setPackageServiceItemId(id);
            WashPackageServiceItemBean update = washPackageServiceItemService.update(bean);

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

    @DeleteMapping("/ajax/pages/WashPackageServiceItems/{id}")
    public String remove(@PathVariable("id") Integer id) {
        JSONObject responseJson = new JSONObject();

        if (id == null) {
            responseJson.put("success", false);
            responseJson.put("message", "PackageServiceItemId是必要欄位");
        } else if (washPackageServiceItemService.findById(id) == null) {
            responseJson.put("success", false);
            responseJson.put("message", "PackageServiceItemId不存在");
        } else {
            boolean result = washPackageServiceItemService.delete(id);

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

    @DeleteMapping("/ajax/pages/WashPackageServiceItems/batch")
    public String removeBatch(@RequestBody List<Integer> ids) {
        JSONObject responseJson = new JSONObject();

        if (ids == null || ids.isEmpty()) {
            responseJson.put("success", false);
            responseJson.put("message", "刪除失敗：未指定刪除項目");
            return responseJson.toString();
        }

        Integer commonServiceId = null;
        for (Integer id : ids) {
            WashPackageServiceItemBean item = washPackageServiceItemService.findById(id);
            if (item == null || item.getService() == null || item.getService().getServiceId() == null) {
                responseJson.put("success", false);
                responseJson.put("message", "不可刪除");
                return responseJson.toString();
            }
            Integer currentServiceId = item.getService().getServiceId();
            if (commonServiceId == null) {
                commonServiceId = currentServiceId;
            } else if (!commonServiceId.equals(currentServiceId)) {
                responseJson.put("success", false);
                responseJson.put("message", "不可刪除");
                return responseJson.toString();
            }
        }

        try {
            for (Integer id : ids) {
                washPackageServiceItemService.delete(id);
            }
            responseJson.put("success", true);
            responseJson.put("message", "批次刪除成功");
        } catch (Exception e) {
            responseJson.put("success", false);
            responseJson.put("message", "刪除失敗：" + e.getMessage());
        }

        return responseJson.toString();
    }

    @GetMapping("/api/wash-package-items")
    public String getAllWashPackageItems() {
        List<WashPackageServiceItemBean> items = washPackageServiceItemService.findAll();
        
        JSONObject responseJson = new JSONObject();
        JSONArray array = new JSONArray();

        if (items != null && !items.isEmpty()) {
            for (WashPackageServiceItemBean item : items) {
                array.put(convertToJson(item));
            }
        }
        
        responseJson.put("list", array);
        return responseJson.toString();
    }

    @GetMapping("/ajax/pages/WashPackageServiceItems/all")
    public String findAllWashPackageItemsAjax() {
        return getAllWashPackageItems();
    }

    private JSONObject convertToJson(WashPackageServiceItemBean item) {
        JSONObject json = new JSONObject()
                .put("packageServiceItemId", item.getPackageServiceItemId())
                .put("typeCode", item.getTypeCode())
                .put("typeName", item.getTypeName())
                .put("periodMinutes", item.getPeriodMinutes())
                .put("quantity", item.getQuantity())
                .put("createdAt", item.getCreatedAt() != null ? item.getCreatedAt().toString() : JSONObject.NULL);

        if (item.getService() != null) {
            json.put("serviceId", item.getService().getServiceId());
        }
        return json;
    }
}
