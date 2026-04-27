package com.subway.lostfound.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class StatisticsDTO {
    
    private long totalItems;
    private long pendingItems;
    private long approvedItems;
    private long rejectedItems;
    private long claimedItems;
    private long returnedItems;
    private double returnRate;
    
    private List<LineStatistics> lineStatistics;
    private List<ItemTypeStatistics> itemTypeStatistics;
    private List<MonthStatistics> monthStatistics;
    
    @Data
    public static class LineStatistics {
        private Long lineId;
        private String lineName;
        private long count;
        private double percentage;
    }
    
    @Data
    public static class ItemTypeStatistics {
        private Long typeId;
        private String typeName;
        private long count;
        private double percentage;
    }
    
    @Data
    public static class MonthStatistics {
        private String month;
        private long total;
        private long returned;
    }
}
