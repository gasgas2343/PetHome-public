package com.system.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "pets")
public class PetsBean extends BaseTimeBean{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserBean user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_type_id")
    private PetTypesBean petTypes;
    private String name;
    private String gender;
    private LocalDate birthday;
    private String breed;
    @Column(name = "weight_kg")
    private BigDecimal weightKg;
    @Column(name = "body_size")
    private String bodySize;
    @Column(name = "personality")
    private String personality;
    @Column(name = "is_active")
    private boolean isActive;
}
