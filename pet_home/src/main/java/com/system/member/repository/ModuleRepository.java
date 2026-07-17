package com.system.member.repository;

import com.system.member.entity.ModuleBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends JpaRepository<ModuleBean, Long> {
}
