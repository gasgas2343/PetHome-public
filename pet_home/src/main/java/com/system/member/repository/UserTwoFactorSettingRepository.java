package com.system.member.repository;

import com.system.member.entity.UserBean;
import com.system.member.entity.UserTwoFactorSettingBean;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTwoFactorSettingRepository extends JpaRepository<UserTwoFactorSettingBean, Long> {
    Optional<UserTwoFactorSettingBean> findByUser_IdAndMethod(Long userId, String method);

    List<UserTwoFactorSettingBean> user(UserBean user);
}
