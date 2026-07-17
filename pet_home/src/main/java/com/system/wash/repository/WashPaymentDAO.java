package com.system.wash.repository;

import java.util.List;
import org.json.JSONObject;
import com.system.wash.entity.WashPaymentBean;

public interface WashPaymentDAO {
    List<WashPaymentBean> find(JSONObject obj);
    long count(JSONObject obj);
}
