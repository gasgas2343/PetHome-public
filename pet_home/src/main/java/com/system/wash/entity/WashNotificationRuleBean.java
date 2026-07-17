package com.system.wash.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "WashNotificationRule")
public class WashNotificationRuleBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_id")
    private Integer ruleId;

    @Column(name = "rule_type")
    private Byte ruleType;

    @Column(name = "notify_channel")
    private Byte notifyChannel;

    @Column(name = "days_before")
    private Byte daysBefore;

    @Column(name = "message_template", length = 500)
    private String messageTemplate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Integer getRuleId() { return ruleId; } public void setRuleId(Integer ruleId) { this.ruleId = ruleId; }
    public Byte getRuleType() { return ruleType; } public void setRuleType(Byte ruleType) { this.ruleType = ruleType; }
    public Byte getNotifyChannel() { return notifyChannel; } public void setNotifyChannel(Byte notifyChannel) { this.notifyChannel = notifyChannel; }
    public Byte getDaysBefore() { return daysBefore; } public void setDaysBefore(Byte daysBefore) { this.daysBefore = daysBefore; }
    public String getMessageTemplate() { return messageTemplate; } public void setMessageTemplate(String messageTemplate) { this.messageTemplate = messageTemplate; }
    public Boolean getIsActive() { return isActive; } public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public String getCreatedBy() { return createdBy; } public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
