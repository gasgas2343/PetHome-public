package com.system.wash.entity;

import java.time.LocalTime;
import jakarta.persistence.*;

@Entity
@Table(name = "WashSlotTemplate")

public class WashSlotTemplateBean {

    @Id
    @Column(name = "slot_index")
    private Byte slotIndex;

    @Column(name = "slot_start")
    private LocalTime slotStart;

    @Column(name = "slot_end")
    private LocalTime slotEnd;
    
    @Column(name = "period_of_day_id") private Integer periodOfDayId;

    // Getters and Setters
    public Byte getSlotIndex() { return slotIndex; }
    public void setSlotIndex(Byte slotIndex) { this.slotIndex = slotIndex; }

    public LocalTime getSlotStart() { return slotStart; }
    public void setSlotStart(LocalTime slotStart) { this.slotStart = slotStart; }

    public LocalTime getSlotEnd() { return slotEnd; }
    public void setSlotEnd(LocalTime slotEnd) { this.slotEnd = slotEnd; }

    public Integer getPeriodOfDayId() { return periodOfDayId; }
    public void setPeriodOfDayId(Integer periodOfDayId) { this.periodOfDayId = periodOfDayId; }
}
