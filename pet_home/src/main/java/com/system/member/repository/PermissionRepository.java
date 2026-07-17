package com.system.member.repository;

import com.system.member.entity.PermissionBean;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionBean, Long> {
    @Query("""
            select p.permissionCode
            from UserRoleBean ur
            join ur.role r
            join RolePermissionBean rp on rp.role = r
            join rp.permission p
            where ur.user.id = :userId
            """)
    List<String> findPermissionCodesByUserId(@Param("userId") Long userId);

    List<PermissionBean> findByPermissionCodeIn(List<String> permissionCodes);

    @EntityGraph(attributePaths = {"module"})
    @Query("""
        select p
        from PermissionBean p
        join p.module m
        order by m.sortOrder asc, p.id asc
    """)
    List<PermissionBean> findAllWithModuleOrderByModuleSortOrder();
}