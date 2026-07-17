package com.system.member.repository;

import com.system.member.entity.UserRoleBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleBean, Long> {
    List<UserRoleBean> findByUser_Id(Long userId);
    void deleteByUser_Id(Long userId);
}
