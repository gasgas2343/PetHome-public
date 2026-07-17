package com.system.member.repository;

import com.system.member.entity.RolePermissionBean;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermissionBean, Long> {

    @Query("""
    select distinct rp
    from RolePermissionBean rp
    join fetch rp.permission p
    join fetch p.module m
    where rp.role.id in :roleIds
""")
    List<RolePermissionBean> findByRoleIdsWithPermissionAndModule(@Param("roleIds") List<Long> roleIds);

    @EntityGraph(attributePaths = "permission")
    List<RolePermissionBean> findByRole_Id(Long roleId);
    void deleteByRole_Id(Long roleId);
}
