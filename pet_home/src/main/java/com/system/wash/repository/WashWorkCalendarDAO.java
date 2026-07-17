package com.system.wash.repository;

import java.util.List;
import org.json.JSONObject;
import com.system.wash.entity.WashWorkCalendarBean;

public interface WashWorkCalendarDAO {
    List<WashWorkCalendarBean> find(JSONObject obj);
    long count(JSONObject obj);
}
