package com.system.wash.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.system.wash.entity.WashPaymentBean;

public interface WashPaymentRepository extends JpaRepository<WashPaymentBean, Integer> {
    long countByPayNoStartingWith(String prefix);
    boolean existsByPayNo(String payNo);
    Optional<WashPaymentBean> findFirstByApptOrderApptOrderIdAndTransactionTypeAndPaymentStatusAndPaymentMethod(
            Integer apptOrderId, Byte transactionType, Byte paymentStatus, Byte paymentMethod);
    java.util.List<WashPaymentBean> findByApptOrderApptOrderId(Integer apptOrderId);
}
