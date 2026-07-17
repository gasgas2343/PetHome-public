package com.system.wash.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.system.wash.entity.WashLinePayTransactionBean;

public interface WashLinePayTransactionRepository extends JpaRepository<WashLinePayTransactionBean, Integer> {
    Optional<WashLinePayTransactionBean> findByOrderId(String orderId);
    Optional<WashLinePayTransactionBean> findFirstByPaymentPaymentIdOrderByLinepayTxnIdDesc(Integer paymentId);
    Optional<WashLinePayTransactionBean> findByTransactionId(Long transactionId);
}
