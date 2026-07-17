package com.system.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.system.shop.entity.Shipment;

import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {
    List<Shipment> findByOrderId(Integer orderId);
    List<Shipment> findByStatus(String status);
}
