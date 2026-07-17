package com.system.wash.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "WashServiceRecord")
public class WashServiceRecordBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Integer recordId;

    @OneToOne
    @JoinColumn(name = "appt_order_id", unique = true)
    private WashAppointmentBean apptOrder;

    @ManyToOne
    @JoinColumn(name = "pet_groomer_id")
    private WashPetGroomerBean petGroomer;

    @Column(name = "record_no", length = 20, unique = true, nullable = false)
    private String recordNo;

    @Column(name = "pet_id")
    private Long petId;

    @Column(name = "skin_status")
    private Byte skinStatus;

    @Column(name = "mood_status")
    private Byte moodStatus;

    @Column(name = "health_comment", length = 500)
    private String healthComment;

    @Column(name = "owner_reply", length = 500)
    private String ownerReply;

    @Column(name = "next_visit_date")
    private LocalDateTime nextVisitDate;

    @Column(name = "next_visit_suggestion", length = 500)
    private String nextVisitSuggestion;

    @Column(name = "note_reply", length = 500)
    private String noteReply;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    // Getters and Setters omitted for brevity but they are standard
    public Integer getRecordId() { return recordId; } public void setRecordId(Integer recordId) { this.recordId = recordId; }
    public WashAppointmentBean getApptOrder() { return apptOrder; } public void setApptOrder(WashAppointmentBean apptOrder) { this.apptOrder = apptOrder; }
    public WashPetGroomerBean getPetGroomer() { return petGroomer; } public void setPetGroomer(WashPetGroomerBean petGroomer) { this.petGroomer = petGroomer; }
    public String getRecordNo() { return recordNo; } public void setRecordNo(String recordNo) { this.recordNo = recordNo; }
    public Long getPetId() { return petId; } public void setPetId(Long petId) { this.petId = petId; }
    public Byte getSkinStatus() { return skinStatus; } public void setSkinStatus(Byte skinStatus) { this.skinStatus = skinStatus; }
    public Byte getMoodStatus() { return moodStatus; } public void setMoodStatus(Byte moodStatus) { this.moodStatus = moodStatus; }
    public String getHealthComment() { return healthComment; } public void setHealthComment(String healthComment) { this.healthComment = healthComment; }
    public String getOwnerReply() { return ownerReply; } public void setOwnerReply(String ownerReply) { this.ownerReply = ownerReply; }
    public LocalDateTime getNextVisitDate() { return nextVisitDate; } public void setNextVisitDate(LocalDateTime nextVisitDate) { this.nextVisitDate = nextVisitDate; }
    public String getNextVisitSuggestion() { return nextVisitSuggestion; } public void setNextVisitSuggestion(String nextVisitSuggestion) { this.nextVisitSuggestion = nextVisitSuggestion; }
    public String getNoteReply() { return noteReply; } public void setNoteReply(String noteReply) { this.noteReply = noteReply; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getCreatedBy() { return createdBy; } public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
