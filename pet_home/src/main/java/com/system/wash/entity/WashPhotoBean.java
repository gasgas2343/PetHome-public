package com.system.wash.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "WashPhoto")
public class WashPhotoBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Integer photoId;

    @ManyToOne
    @JoinColumn(name = "record_id")
    private WashServiceRecordBean record;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "photo_type")
    private Byte photoType;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    @Column(name = "file_name", length = 50)
    private String fileName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Integer getPhotoId() { return photoId; } public void setPhotoId(Integer photoId) { this.photoId = photoId; }
    public WashServiceRecordBean getRecord() { return record; } public void setRecord(WashServiceRecordBean record) { this.record = record; }
    public String getImageUrl() { return imageUrl; } public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getThumbnailUrl() { return thumbnailUrl; } public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public Byte getPhotoType() { return photoType; } public void setPhotoType(Byte photoType) { this.photoType = photoType; }
    public Boolean getIsPublic() { return isPublic; } public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    public String getFileName() { return fileName; } public void setFileName(String fileName) { this.fileName = fileName; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
