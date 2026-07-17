package com.system.wash.repository;

import java.util.List;
import org.json.JSONObject;
import com.system.wash.entity.WashAppointmentBean;

public interface WashAppointmentDAO {
    List<WashAppointmentBean> find(JSONObject obj);
    long count(JSONObject obj);
}
