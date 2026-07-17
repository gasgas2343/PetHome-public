package com.system.wash.service;

import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.system.wash.entity.WashApptDetailBean;
import com.system.wash.entity.WashAppointmentBean;
import com.system.wash.entity.WashServiceBean;
import com.system.wash.entity.WashPackageServiceItemBean;
import com.system.wash.repository.WashApptDetailDAO;
import com.system.wash.repository.WashApptDetailRepository;

@Service
public class WashApptDetailService {

    @Autowired
    private WashApptDetailRepository washApptDetailRepository;

    @Autowired
    private WashApptDetailDAO washApptDetailDAO;

    public List<WashApptDetailBean> find(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            return washApptDetailDAO.find(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long count(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            return washApptDetailDAO.count(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public WashApptDetailBean findById(Integer id) {
        if (id != null) {
            return washApptDetailRepository.findById(id).orElse(null);
        }
        return null;
    }

    public boolean exists(Integer id) {
        return id != null && washApptDetailRepository.existsById(id);
    }

    @Transactional
    public WashApptDetailBean create(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            WashApptDetailBean insert = new WashApptDetailBean();

            mapJsonToBean(obj, insert);
            
            insert.setCreatedAt(LocalDateTime.now());
            insert.setUpdatedAt(LocalDateTime.now());
            
            return washApptDetailRepository.save(insert);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public WashApptDetailBean modify(Integer id, String json) {
        try {
            JSONObject obj = new JSONObject(json);
            Integer apptdetailId = id != null ? id : (obj.isNull("apptdetailId") ? null : obj.getInt("apptdetailId"));

            if (apptdetailId != null) {
                WashApptDetailBean update = washApptDetailRepository.findById(apptdetailId).orElse(null);
                if (update != null) {
                    mapJsonToBean(obj, update);
                    update.setUpdatedAt(LocalDateTime.now());
                    return washApptDetailRepository.save(update);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public boolean remove(Integer id) {
        if (id != null && washApptDetailRepository.existsById(id)) {
            washApptDetailRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void mapJsonToBean(JSONObject obj, WashApptDetailBean bean) {
        // 將前端傳過來的 ID 轉換成對應的 Bean 物件放入關聯中
        if (!obj.isNull("apptOrderId")) {
            WashAppointmentBean appt = new WashAppointmentBean();
            appt.setApptOrderId(obj.getInt("apptOrderId"));
            bean.setApptOrder(appt);
        }
        if (!obj.isNull("serviceId")) {
            WashServiceBean service = new WashServiceBean();
            service.setServiceId(obj.getInt("serviceId"));
            bean.setService(service);
        }
        if (!obj.isNull("packageServiceItemId")) {
            WashPackageServiceItemBean packageServiceItem = new WashPackageServiceItemBean();
            packageServiceItem.setPackageServiceItemId(obj.getInt("packageServiceItemId"));
            bean.setPackageServiceItem(packageServiceItem);
        }
        if (!obj.isNull("consumedPoints")) bean.setConsumedPoints(obj.getInt("consumedPoints"));
        if (!obj.isNull("unitPrice")) bean.setUnitPrice(obj.getInt("unitPrice"));
        if (!obj.isNull("itemAmount")) bean.setItemAmount(obj.getInt("itemAmount"));
    }
}
