package com.system.wash.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "WashAppointmentStatusLog")
public class WashAppointmentStatusLogBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

    @ManyToOne
    @JoinColumn(name = "appt_order_id")
    private WashAppointmentBean apptOrder;

    @Column(name = "from_status")
    private Byte fromStatus;

    @Column(name = "to_status")
    private Byte toStatus;

    @Column(name = "changed_by", length = 100)
    private String changedBy;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    @Column(name = "reason", length = 255)
    private String reason;

    @Override
    public String toString() {
        return "WashAppointmentStatusLogBean [logId=" + logId + ", apptOrder=" + apptOrder + ", fromStatus="
                + fromStatus + ", toStatus=" + toStatus + "]";
    }

    // Getters and Setters
    public Integer getLogId() { return logId; }
    public void setLogId(Integer logId) { this.logId = logId; }

    public WashAppointmentBean getApptOrder() { return apptOrder; }
    public void setApptOrder(WashAppointmentBean apptOrder) { this.apptOrder = apptOrder; }

    public Byte getFromStatus() { return fromStatus; }
    public void setFromStatus(Byte fromStatus) { this.fromStatus = fromStatus; }

    public Byte getToStatus() { return toStatus; }
    public void setToStatus(Byte toStatus) { this.toStatus = toStatus; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
