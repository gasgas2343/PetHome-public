package com.system.wash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.system.wash.entity.PetTypesBean;

public interface PetTypesRepository extends JpaRepository<PetTypesBean, Long> {
}
