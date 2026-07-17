package com.system.member.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_two_factor_settings")
public class UserTwoFactorSettingBean extends BaseTimeBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserBean user;

    @NotNull
    @ColumnDefault("'TOTP'")
    @Column(name = "method", nullable = false)
    private String method;

    @NotNull
    @Column(name = "secret_encrypted", nullable = false)
    private String secretEncrypted;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

}