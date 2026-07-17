package com.system.wash.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.system.wash.entity.WashCalendarPeriodBean;

@Repository
public interface WashCalendarPeriodRepository extends JpaRepository<WashCalendarPeriodBean, Integer> {
    List<WashCalendarPeriodBean> findByCalendarId(Integer calendarId);

    void deleteByCalendarId(Integer calendarId);
}
