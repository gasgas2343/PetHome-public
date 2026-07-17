package com.system.wash.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.system.wash.entity.WashCalendarPeriodBean;
import com.system.wash.repository.WashCalendarPeriodRepository;

@Service
@Transactional
public class WashCalendarPeriodService {

    @Autowired
    private WashCalendarPeriodRepository washCalendarPeriodRepository;

    public List<WashCalendarPeriodBean> findByCalendarId(Integer calendarId) {
        return washCalendarPeriodRepository.findByCalendarId(calendarId);
    }

    public WashCalendarPeriodBean save(WashCalendarPeriodBean bean) {
        return washCalendarPeriodRepository.save(bean);
    }
}
