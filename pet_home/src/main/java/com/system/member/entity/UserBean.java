package com.system.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class UserBean extends BaseTimeBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfilesBean profiles;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserAddressesBean> addresses;
    private String email;
    @Column(name = "pending_email")
    private String pendingEmail;
    @Column(name = "password_hash")
    private String passwordHash;
    private String status = "ACTIVE";
    @Column(name = "status_until")
    private LocalDateTime statusUntil;
    @Column(name = "email_verified")
    private boolean emailVerified = false;
    @Column(name = "two_factor_enabled")
    private boolean twoFactorEnabled = false;
    @Column(name = "failed_login_count")
    private int failedLoginCount = 0;
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    @Column(name = "token_version")
    private int tokenVersion = 0;
    @Column(name = "permission_version")
    private int permissionVersion = 0;
    @Column(name = "has_local_password")
    private boolean hasLocalPassword = true;
}
