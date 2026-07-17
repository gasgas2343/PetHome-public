package com.system.wash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.system.wash.entity.WashPeriodOfDayBean;


@Repository
public interface WashPeriodOfDayRepository extends JpaRepository<WashPeriodOfDayBean, Integer> {
}
