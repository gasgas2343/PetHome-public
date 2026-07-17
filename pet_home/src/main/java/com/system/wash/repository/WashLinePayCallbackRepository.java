package com.system.wash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.system.wash.entity.WashLinePayCallbackBean;

public interface WashLinePayCallbackRepository extends JpaRepository<WashLinePayCallbackBean, Integer> {
}
