package com.subway.lostfound.dto;

import com.subway.lostfound.entity.enums.ItemStatus;
import com.subway.lostfound.entity.enums.ItemType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LostFoundItemResponse {
    
    private Long id;
    private ItemType itemType;
    private String title;
    private String description;
    private Long itemTypeId;
    private String itemTypeName;
    private Long subwayLineId;
    private String subwayLineName;
    private String stationName;
    private LocalDateTime lostFoundTime;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String imageUrls;
    private Long publisherId;
    private String publisherName;
    private ItemStatus status;
    private String rejectReason;
    private String adminRemark;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
