package com.system.wash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.system.wash.entity.WashDepositPolicyBean;

public interface WashDepositPolicyRepository extends JpaRepository<WashDepositPolicyBean, Integer> {
}
