package com.system.wash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.system.wash.entity.WashServiceBean;

@Repository
public interface WashServiceRepository extends JpaRepository<WashServiceBean, Integer> {
    boolean existsByServiceName(String serviceName);
    boolean existsByServiceNameAndServiceIdNot(String serviceName, Integer serviceId);
}
