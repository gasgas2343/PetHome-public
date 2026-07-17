package com.system.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_profiles")
public class UserProfilesBean extends BaseTimeBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    private UserBean user;
    @Column(name = "full_name")
    private String fullName;
    private String nickname;
    private String phone;
    private LocalDate birthday;
    @Column(name = "avatar_url")
    private String avatarUrl;
    @Column(name = "points_balance")
    private Integer pointsBalance = 0;
}
