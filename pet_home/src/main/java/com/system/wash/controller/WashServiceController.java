package com.system.wash.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import com.system.wash.entity.WashServiceBean;
import com.system.wash.service.WashServiceService;

@RestController
@CrossOrigin
public class WashServiceController {

    private static final Map<Long, WashServiceState> userStateMap = new ConcurrentHashMap<>();

    public static class WashServiceState {
        private List<Integer> basicIds;
        private Integer packageId;
        private String rawStateJson;

        public List<Integer> getBasicIds() {
            return basicIds;
        }

        public void setBasicIds(List<Integer> basicIds) {
            this.basicIds = basicIds;
        }

        public Integer getPackageId() {
            return packageId;
        }

        public void setPackageId(Integer packageId) {
            this.packageId = packageId;
        }

        public String getRawStateJson() {
            return rawStateJson;
        }

        public void setRawStateJson(String rawStateJson) {
            this.rawStateJson = rawStateJson;
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        } else if (principal instanceof Number) {
            return ((Number) principal).longValue();
        } else if (principal != null) {
            try {
                return Long.parseLong(principal.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @PostMapping("/ajax/pages/WashServices/saveState")
    public String saveState(@RequestBody String body) {
        JSONObject responseJson = new JSONObject();
        Long userId = getCurrentUserId();
        if (userId == null) {
            responseJson.put("success", false);
            responseJson.put("message", "未登入或無法取得使用者資訊");
            return responseJson.toString();
        }

        try {
            JSONObject requestJson = new JSONObject(body);
            WashServiceState state = new WashServiceState();

            if (requestJson.has("basicIds")) {
                JSONArray basicIdsArray = requestJson.getJSONArray("basicIds");
                List<Integer> basicIds = new ArrayList<>();
                for (int i = 0; i < basicIdsArray.length(); i++) {
                    basicIds.add(basicIdsArray.getInt(i));
                }
                state.setBasicIds(basicIds);
            }
            if (requestJson.has("packageId") && !requestJson.isNull("packageId")) {
                state.setPackageId(requestJson.getInt("packageId"));
            }
            state.setRawStateJson(body);

            userStateMap.put(userId, state);

            responseJson.put("success", true);
            responseJson.put("message", "狀態已成功保留");
        } catch (Exception e) {
            responseJson.put("success", false);
            responseJson.put("message", "格式錯誤：" + e.getMessage());
        }

        return responseJson.toString();
    }

    @GetMapping("/ajax/pages/WashServices/getState")
    public String getState() {
        JSONObject responseJson = new JSONObject();
        Long userId = getCurrentUserId();
        if (userId == null) {
            responseJson.put("success", false);
            responseJson.put("message", "未登入或無法取得使用者資訊");
            return responseJson.toString();
        }

        WashServiceState state = userStateMap.get(userId);
        if (state == null) {
            responseJson.put("success", true);
            responseJson.put("state", JSONObject.NULL);
        } else {
            responseJson.put("success", true);
            responseJson.put("state", new JSONObject(state.getRawStateJson()));
        }

        return responseJson.toString();
    }

    @PostMapping("/ajax/pages/WashServices/clearState")
    public String clearState() {
        JSONObject responseJson = new JSONObject();
        Long userId = getCurrentUserId();
        if (userId == null) {
            responseJson.put("success", false);
            responseJson.put("message", "未登入或無法取得使用者資訊");
            return responseJson.toString();
        }

        userStateMap.remove(userId);

        responseJson.put("success", true);
        responseJson.put("message", "狀態已清除");
        return responseJson.toString();
    }

    public static void clearState(Long userId) {
        if (userId != null) {
            userStateMap.remove(userId);
        }
    }

    @Autowired
    private WashServiceService washServiceService;

    @PostMapping("/ajax/pages/WashServices/find")
    public String find(@RequestBody(required = false) String body) {
        JSONObject responseJson = new JSONObject();

        // 目前 Service 無實作分頁或條件查詢，暫以 findAll 替代
        List<WashServiceBean> services = washServiceService.findAll();
        long count = services != null ? services.size() : 0;
        responseJson.put("count", count);

        JSONArray array = new JSONArray();
        if (services != null && !services.isEmpty()) {
            for (WashServiceBean service : services) {
                array.put(convertToJson(service));
            }
        }

        responseJson.put("list", array);
        return responseJson.toString();
    }

    @GetMapping("/ajax/pages/WashServices/{id}")
    public String findById(@PathVariable("id") Integer id) {
        JSONObject responseJson = new JSONObject();
        JSONArray array = new JSONArray();

        WashServiceBean service = washServiceService.findById(id);
        if (service != null) {
            array.put(convertToJson(service));
        }

        responseJson.put("list", array);
        return responseJson.toString();
    }

    @PostMapping("/ajax/pages/WashServices")
    public String create(@RequestBody WashServiceBean bean) {
        JSONObject responseJson = new JSONObject();

        if (bean != null && bean.getServiceName() != null) {
            String trimmedName = bean.getServiceName().trim();
            if (washServiceService.existsByServiceName(trimmedName)) {
                responseJson.put("success", false);
                responseJson.put("message", "新增失敗：服務名稱不能重複");
                return responseJson.toString();
            }
            bean.setServiceName(trimmedName);
        }

        WashServiceBean result = washServiceService.insert(bean);

        if (result == null) {
            responseJson.put("success", false);
            responseJson.put("message", "新增失敗");
        } else {
            responseJson.put("success", true);
            responseJson.put("message", "新增成功");
            responseJson.put("serviceId", result.getServiceId());
        }

        return responseJson.toString();
    }

    @PutMapping("/ajax/pages/WashServices/{id}")
    public String modify(@PathVariable("id") Integer id, @RequestBody WashServiceBean bean) {
        JSONObject responseJson = new JSONObject();

        if (id == null) {
            responseJson.put("success", false);
            responseJson.put("message", "ServiceId是必要欄位");
        } else if (washServiceService.findById(id) == null) {
            responseJson.put("success", false);
            responseJson.put("message", "ServiceId不存在");
        } else {
            if (bean != null && bean.getServiceName() != null) {
                String trimmedName = bean.getServiceName().trim();
                if (washServiceService.existsByServiceNameAndServiceIdNot(trimmedName, id)) {
                    responseJson.put("success", false);
                    responseJson.put("message", "修改失敗：服務名稱不能重複");
                    return responseJson.toString();
                }
                bean.setServiceName(trimmedName);
            }
            bean.setServiceId(id);
            WashServiceBean update = washServiceService.update(bean);

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


    @DeleteMapping("/ajax/pages/WashServices/{id}")
    public String remove(@PathVariable("id") Integer id) {
        JSONObject responseJson = new JSONObject();

        if (id == null) {
            responseJson.put("success", false);
            responseJson.put("message", "ServiceId是必要欄位");
        } else if (washServiceService.findById(id) == null) {
            responseJson.put("success", false);
            responseJson.put("message", "ServiceId不存在");
        } else {
            boolean result = washServiceService.delete(id);

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

    @GetMapping("/api/wash-services")
    public String getAllWashServices() {
        List<WashServiceBean> services = washServiceService.findAll();

        JSONObject responseJson = new JSONObject();
        JSONArray array = new JSONArray();

        if (services != null && !services.isEmpty()) {
            for (WashServiceBean service : services) {
                array.put(convertToJson(service));
            }
        }

        responseJson.put("list", array);
        return responseJson.toString();
    }

    @GetMapping("/ajax/pages/WashServices/all")
    public String findAllWashServicesAjax() {
        return getAllWashServices();
    }

    private JSONObject convertToJson(WashServiceBean service) {
        return new JSONObject()
                .put("serviceId", service.getServiceId())
                .put("serviceName", service.getServiceName())
                .put("fullPrice", service.getFullPrice())
                .put("onsalePrice", service.getOnsalePrice())
                .put("promotionPrice", service.getPromotionPrice())
                .put("promoStart",
                        service.getPromoStart() != null ? service.getPromoStart().toString() : JSONObject.NULL)
                .put("promoEnd", service.getPromoEnd() != null ? service.getPromoEnd().toString() : JSONObject.NULL)
                .put("isActive", service.getIsActive())
                .put("memberPoint", service.getMemberPoint());
    }
}
