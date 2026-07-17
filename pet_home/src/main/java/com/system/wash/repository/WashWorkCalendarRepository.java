package com.system.wash.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.wash.entity.WashWorkCalendarBean;
import java.util.List;
import java.time.LocalDate;



public interface WashWorkCalendarRepository extends JpaRepository<WashWorkCalendarBean, Integer> {
    WashWorkCalendarBean findByWorkDate(LocalDate workDate);
}
