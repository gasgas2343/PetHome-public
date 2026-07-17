package com.system.wash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.system.wash.entity.WashMemberPointBean;

public interface WashMemberPointRepository extends JpaRepository<WashMemberPointBean, Integer> {
}
