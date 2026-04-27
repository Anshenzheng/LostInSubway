package com.subway.lostfound.entity;

import com.subway.lostfound.entity.enums.ItemStatus;
import com.subway.lostfound.entity.enums.ItemType;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "lost_found_items")
public class LostFoundItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @Column(name = "item_type_id")
    private Long itemTypeId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_type_id", insertable = false, updatable = false)
    private ItemTypeCategory typeCategory;
    
    @Column(name = "subway_line_id")
    private Long subwayLineId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subway_line_id", insertable = false, updatable = false)
    private SubwayLine subwayLine;
    
    @Column(name = "station_name")
    private String stationName;
    
    @Column(name = "lost_found_time")
    private LocalDateTime lostFoundTime;
    
    @Column(name = "contact_name")
    private String contactName;
    
    @Column(name = "contact_phone")
    private String contactPhone;
    
    @Column(name = "contact_email")
    private String contactEmail;
    
    @Column(name = "image_urls", columnDefinition = "TEXT")
    private String imageUrls;
    
    @Column(name = "publisher_id", nullable = false)
    private Long publisherId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", insertable = false, updatable = false)
    private User publisher;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus status = ItemStatus.PENDING;
    
    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;
    
    @Column(name = "admin_remark", columnDefinition = "TEXT")
    private String adminRemark;
    
    @Column(name = "view_count")
    private Integer viewCount = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (viewCount == null) {
            viewCount = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
