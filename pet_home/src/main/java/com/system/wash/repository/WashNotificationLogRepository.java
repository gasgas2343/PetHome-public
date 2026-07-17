package com.system.wash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.system.wash.entity.WashNotificationLogBean;

public interface WashNotificationLogRepository extends JpaRepository<WashNotificationLogBean, Integer> {
}
