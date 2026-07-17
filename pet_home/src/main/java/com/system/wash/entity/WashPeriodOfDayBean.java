package com.system.wash.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Time;

@Entity
@Table(name = "WashPeriodOfDay")
public class WashPeriodOfDayBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "period_of_day_id")
    private Integer periodOfDayId;

    @Column(name = "period_name", length = 50)
    private String periodName;

    @Column(name = "default_start")
    private Time defaultStart;

    @Column(name = "default_end")
    private Time defaultEnd;

    @Column(name = "is_active")
    private Boolean isActive;

    public Integer getPeriodOfDayId() {
        return periodOfDayId;
    }

    public void setPeriodOfDayId(Integer periodOfDayId) {
        this.periodOfDayId = periodOfDayId;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public Time getDefaultStart() {
        return defaultStart;
    }

    public void setDefaultStart(Time defaultStart) {
        this.defaultStart = defaultStart;
    }

    public Time getDefaultEnd() {
        return defaultEnd;
    }

    public void setDefaultEnd(Time defaultEnd) {
        this.defaultEnd = defaultEnd;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
