package com.system.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_sessions")
public class UserSessionBean extends BaseTimeBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 使用者
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserBean user;

    // Session Token (資料庫 NOT NULL)
    // @Column(name = "session_token", nullable = false)
    // private String sessionToken;

    // Refresh Token Hash
    @Column(name = "refresh_token_hash")
    private String refreshTokenHash;

    // 裝置名稱
    @Column(name = "device_name")
    private String deviceName;

    // IP
    @Column(name = "ip_address")
    private String ipAddress;

    // User Agent
    @Column(name = "user_agent")
    private String userAgent;

    // 是否有效
    @Column(name = "is_active")
    private Boolean isActive = false;

    // 登入時間
    @Column(name = "login_at", nullable = false)
    private LocalDateTime loginAt;

    // 最後使用時間
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    // 到期時間
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // 撤銷時間
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    // 撤銷原因
    @Column(name = "revoked_reason")
    private String revokedReason;
}