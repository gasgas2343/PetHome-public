package com.system.wash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.system.wash.entity.WashMemberPointBatchBean;

public interface WashMemberPointBatchRepository extends JpaRepository<WashMemberPointBatchBean, Integer> {
}
