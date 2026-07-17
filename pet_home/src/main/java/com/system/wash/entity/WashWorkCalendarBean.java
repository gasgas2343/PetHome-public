package com.system.wash.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "WashWorkCalendar")
public class WashWorkCalendarBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_id")
    private Integer calendarId;

    @Column(name = "work_date", unique = true, nullable = false)
    private LocalDate workDate;

    @Column(name = "is_workday", nullable = false)
    private Boolean isWorkday = true;

    @Column(name = "day_type")
    private Byte dayType;

    @Column(name = "note", length = 200)
    private String note;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Integer getCalendarId() { return calendarId; } public void setCalendarId(Integer calendarId) { this.calendarId = calendarId; }
    public LocalDate getWorkDate() { return workDate; } public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }
    public Boolean getIsWorkday() { return isWorkday; } public void setIsWorkday(Boolean isWorkday) { this.isWorkday = isWorkday; }
    public Byte getDayType() { return dayType; } public void setDayType(Byte dayType) { this.dayType = dayType; }
    public String getNote() { return note; } public void setNote(String note) { this.note = note; }
    public String getCreatedBy() { return createdBy; } public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
