package com.system.member.repository;

import com.system.member.entity.PetTypesBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetTypeRepository extends JpaRepository<PetTypesBean, Long> {
}
