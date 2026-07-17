package com.system.member.repository;

import com.system.member.entity.PetsBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<PetsBean, Long> {
 List<PetsBean> findByUser_IdAndIsActiveTrue(Long userId);
 Optional<PetsBean> findByIdAndUser_IdAndIsActiveTrue(Long petId, Long userId);
}
