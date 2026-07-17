package com.system.wash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.system.wash.entity.WashApptDetailBean;

public interface WashApptDetailRepository extends JpaRepository<WashApptDetailBean, Integer> {
    java.util.List<WashApptDetailBean> findByApptOrderApptOrderId(Integer apptOrderId);
}
