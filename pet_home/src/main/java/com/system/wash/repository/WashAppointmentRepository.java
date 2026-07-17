package com.system.wash.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.system.wash.entity.WashAppointmentBean;

public interface WashAppointmentRepository extends JpaRepository<WashAppointmentBean, Integer> {
    long countByApptNoStartingWith(String prefix);
    boolean existsByApptNo(String apptNo);

    @Query("SELECT a FROM WashAppointmentBean a WHERE a.depositStatus = 0 AND a.apptStatus < 3 AND a.depositDeadline IS NOT NULL AND a.depositDeadline < :now")
    List<WashAppointmentBean> findExpiredAppointments(@Param("now") LocalDateTime now);
}
