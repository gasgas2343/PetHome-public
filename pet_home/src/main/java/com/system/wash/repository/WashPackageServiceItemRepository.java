package com.system.wash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.system.wash.entity.WashPackageServiceItemBean;

@Repository
public interface WashPackageServiceItemRepository extends JpaRepository<WashPackageServiceItemBean, Integer> {
    boolean existsByServiceServiceIdAndTypeCode(Integer serviceId, String typeCode);
    void deleteByServiceServiceId(Integer serviceId);
}
