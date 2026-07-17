package com.system.wash.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "WashNotificationLog")
public class WashNotificationLogBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

    @ManyToOne
    @JoinColumn(name = "rule_id")
    private WashNotificationRuleBean rule;

    @Column(name = "member_id")
    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private WashMemberPointBatchBean batch;

    @ManyToOne
    @JoinColumn(name = "appt_order_id")
    private WashAppointmentBean apptOrder;

    @Column(name = "notify_channel")
    private Byte notifyChannel;

    @Column(name = "message_content", length = 500)
    private String messageContent;

    @Column(name = "send_status")
    private Byte sendStatus;

    @Column(name = "send_at")
    private LocalDateTime sendAt;

    @Column(name = "error_msg", length = 255)
    private String errorMsg;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Getters and Setters
    public Integer getLogId() { return logId; } public void setLogId(Integer logId) { this.logId = logId; }
    public WashNotificationRuleBean getRule() { return rule; } public void setRule(WashNotificationRuleBean rule) { this.rule = rule; }
    public Long getMemberId() { return memberId; } public void setMemberId(Long memberId) { this.memberId = memberId; }
    public WashMemberPointBatchBean getBatch() { return batch; } public void setBatch(WashMemberPointBatchBean batch) { this.batch = batch; }
    public WashAppointmentBean getApptOrder() { return apptOrder; } public void setApptOrder(WashAppointmentBean apptOrder) { this.apptOrder = apptOrder; }
    public Byte getNotifyChannel() { return notifyChannel; } public void setNotifyChannel(Byte notifyChannel) { this.notifyChannel = notifyChannel; }
    public String getMessageContent() { return messageContent; } public void setMessageContent(String messageContent) { this.messageContent = messageContent; }
    public Byte getSendStatus() { return sendStatus; } public void setSendStatus(Byte sendStatus) { this.sendStatus = sendStatus; }
    public LocalDateTime getSendAt() { return sendAt; } public void setSendAt(LocalDateTime sendAt) { this.sendAt = sendAt; }
    public String getErrorMsg() { return errorMsg; } public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
