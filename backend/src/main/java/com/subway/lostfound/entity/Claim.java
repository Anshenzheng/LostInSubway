package com.subway.lostfound.entity;

import com.subway.lostfound.entity.enums.ClaimStatus;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "claims")
public class Claim {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    private LostFoundItem item;
    
    @Column(name = "claimer_id", nullable = false)
    private Long claimerId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claimer_id", insertable = false, updatable = false)
    private User claimer;
    
    @Column(name = "claim_reason", columnDefinition = "TEXT", nullable = false)
    private String claimReason;
    
    @Column(name = "proof_images", columnDefinition = "TEXT")
    private String proofImages;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status = ClaimStatus.PENDING;
    
    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;
    
    @Column(name = "admin_remark", columnDefinition = "TEXT")
    private String adminRemark;
    
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
