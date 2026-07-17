package com.system.member.repository;

import com.system.member.entity.RoleBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleBean, Long> {
    Optional<RoleBean> findByRoleCode(String roleCode);
    List<RoleBean> findByRoleCodeIn(List<String> roleCodes);
}
