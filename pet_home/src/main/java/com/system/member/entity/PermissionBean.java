package com.system.member.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "permissions")
public class PermissionBean extends BaseTimeBean{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private ModuleBean module;

    @NotNull
    @Column(name = "permission_code")
    private String permissionCode;

    @NotNull
    @Column(name = "permission_name")
    private String permissionName;

    @Column(name = "api_method")
    private String apiMethod;

    @Size(max = 255)
    @Column(name = "api_path")
    private String apiPath;
}