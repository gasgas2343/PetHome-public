package com.system.wash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.system.wash.entity.WashServiceRecordBean;

public interface WashServiceRecordRepository extends JpaRepository<WashServiceRecordBean, Integer> {
}
