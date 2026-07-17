package com.system.wash.repository;

import java.util.List;
import org.json.JSONObject;
import com.system.wash.entity.WashApptDetailBean;

public interface WashApptDetailDAO {
    List<WashApptDetailBean> find(JSONObject obj);
    long count(JSONObject obj);
}
