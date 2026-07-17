package com.system.wash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.system.wash.entity.WashPetGroomerBean;

public interface WashPetGroomerRepository extends JpaRepository<WashPetGroomerBean, Integer> {
}
