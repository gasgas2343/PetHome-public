package com.system.wash.service;

import java.sql.Time;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.system.wash.entity.WashPeriodOfDayBean;
import com.system.wash.repository.WashPeriodOfDayRepository;

@Service
@Transactional
public class WashPeriodOfDayService {

    @Autowired
    private WashPeriodOfDayRepository washPeriodOfDayRepository;

    public long count(String body) {
        // 目前回傳總數。如需根據 body 做條件搜尋，可在此處擴充 Specification 或自訂 Query
        return washPeriodOfDayRepository.count();
    }

    public List<WashPeriodOfDayBean> find(String body) {
        // 目前回傳全部。如需分頁或條件篩選，可解析 body 後呼叫對應的 repository 方法
        return washPeriodOfDayRepository.findAll();
    }

    public WashPeriodOfDayBean findById(Integer id) {
        if (id != null) {
            Optional<WashPeriodOfDayBean> optional = washPeriodOfDayRepository.findById(id);
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        return null;
    }

    public WashPeriodOfDayBean create(String body) {
        try {
            JSONObject obj = new JSONObject(body);
            WashPeriodOfDayBean bean = new WashPeriodOfDayBean();

            if (!obj.isNull("periodName")) {
                bean.setPeriodName(obj.getString("periodName"));
            }
            if (!obj.isNull("defaultStart")) {
                bean.setDefaultStart(parseTime(obj.getString("defaultStart")));
            }
            if (!obj.isNull("defaultEnd")) {
                bean.setDefaultEnd(parseTime(obj.getString("defaultEnd")));
            }
            if (!obj.isNull("isActive")) {
                bean.setIsActive(obj.getBoolean("isActive"));
            }

            return washPeriodOfDayRepository.save(bean);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean exists(Integer id) {
        if (id != null) {
            return washPeriodOfDayRepository.existsById(id);
        }
        return false;
    }

    public WashPeriodOfDayBean modify(Integer id, String body) {
        try {
            WashPeriodOfDayBean bean = findById(id);
            if (bean != null) {
                JSONObject obj = new JSONObject(body);

                if (!obj.isNull("periodName")) {
                    bean.setPeriodName(obj.getString("periodName"));
                }
                if (!obj.isNull("defaultStart")) {
                    bean.setDefaultStart(parseTime(obj.getString("defaultStart")));
                }
                if (!obj.isNull("defaultEnd")) {
                    bean.setDefaultEnd(parseTime(obj.getString("defaultEnd")));
                }
                if (!obj.isNull("isActive")) {
                    bean.setIsActive(obj.getBoolean("isActive"));
                }

                return washPeriodOfDayRepository.save(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean remove(Integer id) {
        if (id != null && washPeriodOfDayRepository.existsById(id)) {
            washPeriodOfDayRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<WashPeriodOfDayBean> findAllPeriodOfDays() {
        return washPeriodOfDayRepository.findAll();
    }

    /**
     * 解析時間字串成 java.sql.Time
     * 若前端傳入的字串為 HH:mm 格式，需自動補齊秒數 :00，否則 Time.valueOf 會報錯
     */
    private Time parseTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) return null;
        if (timeStr.length() == 5) { // e.g. "13:30"
            timeStr += ":00";
        }
        return Time.valueOf(timeStr);
    }
}
