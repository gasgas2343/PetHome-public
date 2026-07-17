package com.system.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public class BaseTimeBean {
    @Column(name = "created_at")
    private LocalDateTime createdDate;
    @Column(name = "updated_at")
    private LocalDateTime updatedDate;

    @PrePersist
    public void preCreatedData() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        this.createdDate = now;
        this.updatedDate = now;
    }
    @PreUpdate
    public void preUpdateData(){
        this.updatedDate = LocalDateTime.now().withNano(0);
    }
}
