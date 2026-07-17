package com.system.member.repository;

import com.system.member.entity.UserProfilesBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfilesBean, Long> {
    Optional<UserProfilesBean> findByUserId(long userId);
}
