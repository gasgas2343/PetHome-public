package com.system.wash.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.system.wash.entity.WashScheduleBean;

public interface WashScheduleRepository extends JpaRepository<WashScheduleBean, Integer> {
    List<WashScheduleBean> findByWorkDate(LocalDate workDate);
    List<WashScheduleBean> findBySlotIndex(Byte slotIndex);
    Optional<WashScheduleBean> findByWorkDateAndSlotIndex(LocalDate workDate, Byte slotIndex);
    List<WashScheduleBean> findByApptOrderId(Integer apptOrderId);
}
