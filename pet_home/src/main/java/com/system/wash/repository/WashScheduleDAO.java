package com.system.wash.repository;

import java.util.List;
import org.json.JSONObject;
import com.system.wash.entity.WashScheduleBean;

public interface WashScheduleDAO {
    List<WashScheduleBean> find(JSONObject obj);
    long count(JSONObject obj);
}
