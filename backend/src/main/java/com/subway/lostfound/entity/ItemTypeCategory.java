package com.subway.lostfound.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "item_types")
public class ItemTypeCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "type_name", nullable = false)
    private String typeName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String icon;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Enumerated(EnumType.STRING)
    private com.subway.lostfound.entity.enums.LineStatus status = com.subway.lostfound.entity.enums.LineStatus.ACTIVE;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
