package com.system.member.repository;

import com.system.member.entity.UserBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<UserBean, Long> {
    boolean existsByEmail(String email);

    boolean existsByPendingEmail(String pendingEmail);

    Optional<UserBean> findByEmail(String email);

    @Modifying
    @Query("""
    update UserBean u
    set u.permissionVersion = u.permissionVersion + 1,
        u.updatedDate = CURRENT_TIMESTAMP
    where u.id in (
        select ur.user.id
        from UserRoleBean ur
        where ur.role.id = :roleId
    )
""")
    int increasePermissionVersionByRoleId(@Param("roleId") Long roleId);

    List<UserBean> findAllByOrderByCreatedDateDesc();
}
