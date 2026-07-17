package com.system.wash.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "WashLineNotify")
public class WashLineNotifyBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_notify_id")
    private Integer lineNotifyId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "line_openid", length = 255, unique = true)
    private String lineOpenid;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "picture_url", length = 500)
    private String pictureUrl;

    @Column(name = "follow_status")
    private Byte followStatus;

    @Column(name = "last_interaction_at")
    private LocalDateTime lastInteractionAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Integer getLineNotifyId() { return lineNotifyId; } public void setLineNotifyId(Integer lineNotifyId) { this.lineNotifyId = lineNotifyId; }
    public Long getMemberId() { return memberId; } public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getLineOpenid() { return lineOpenid; } public void setLineOpenid(String lineOpenid) { this.lineOpenid = lineOpenid; }
    public String getDisplayName() { return displayName; } public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getPictureUrl() { return pictureUrl; } public void setPictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; }
    public Byte getFollowStatus() { return followStatus; } public void setFollowStatus(Byte followStatus) { this.followStatus = followStatus; }
    public LocalDateTime getLastInteractionAt() { return lastInteractionAt; } public void setLastInteractionAt(LocalDateTime lastInteractionAt) { this.lastInteractionAt = lastInteractionAt; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
