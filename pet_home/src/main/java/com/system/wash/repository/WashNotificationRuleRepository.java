package com.system.wash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.system.wash.entity.WashNotificationRuleBean;

public interface WashNotificationRuleRepository extends JpaRepository<WashNotificationRuleBean, Integer> {
}
