package com.system.wash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.system.wash.entity.WashAppointmentStatusLogBean;

public interface WashAppointmentStatusLogRepository extends JpaRepository<WashAppointmentStatusLogBean, Integer> {
}
