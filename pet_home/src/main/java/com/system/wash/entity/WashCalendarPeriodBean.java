package com.system.wash.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "WashCalendarPeriod")
public class WashCalendarPeriodBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_period_id")
    private Integer calendarPeriodId;

    @Column(name = "calendar_id")
    private Integer calendarId;

    @Column(name = "period_of_day_id")
    private Integer periodOfDayId;

    @Column(name = "is_active")
    private Boolean isActive;

    public Integer getCalendarPeriodId() {
        return calendarPeriodId;
    }

    public void setCalendarPeriodId(Integer calendarPeriodId) {
        this.calendarPeriodId = calendarPeriodId;
    }

    public Integer getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Integer calendarId) {
        this.calendarId = calendarId;
    }

    public Integer getPeriodOfDayId() {
        return periodOfDayId;
    }

    public void setPeriodOfDayId(Integer periodOfDayId) {
        this.periodOfDayId = periodOfDayId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
