package com.subway.lostfound.dto;

import com.subway.lostfound.entity.enums.ClaimStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClaimResponse {
    
    private Long id;
    private Long itemId;
    private String itemTitle;
    private Long claimerId;
    private String claimerName;
    private String claimReason;
    private String proofImages;
    private ClaimStatus status;
    private String rejectReason;
    private String adminRemark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
