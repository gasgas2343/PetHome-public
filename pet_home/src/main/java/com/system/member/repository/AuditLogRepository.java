package com.system.member.repository;

import com.system.member.entity.AuditLogBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogBean, Long> {
    List<AuditLogBean>  findAllByOrderByCreatedAtDesc();

}
