package com.subway.lostfound.service;

import com.subway.lostfound.dto.StatisticsDTO;
import com.subway.lostfound.entity.ItemTypeCategory;
import com.subway.lostfound.entity.SubwayLine;
import com.subway.lostfound.entity.enums.ItemStatus;
import com.subway.lostfound.repository.ItemTypeCategoryRepository;
import com.subway.lostfound.repository.LostFoundItemRepository;
import com.subway.lostfound.repository.SubwayLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {
    
    @Autowired
    private LostFoundItemRepository itemRepository;
    
    @Autowired
    private SubwayLineRepository subwayLineRepository;
    
    @Autowired
    private ItemTypeCategoryRepository itemTypeCategoryRepository;
    
    public StatisticsDTO getStatistics() {
        StatisticsDTO statistics = new StatisticsDTO();
        
        long totalItems = itemRepository.count();
        long pendingItems = itemRepository.countByStatus(ItemStatus.PENDING);
        long approvedItems = itemRepository.countByStatus(ItemStatus.APPROVED);
        long rejectedItems = itemRepository.countByStatus(ItemStatus.REJECTED);
        long claimedItems = itemRepository.countByStatus(ItemStatus.CLAIMED);
        long returnedItems = itemRepository.countByStatus(ItemStatus.RETURNED);
        
        statistics.setTotalItems(totalItems);
        statistics.setPendingItems(pendingItems);
        statistics.setApprovedItems(approvedItems);
        statistics.setRejectedItems(rejectedItems);
        statistics.setClaimedItems(claimedItems);
        statistics.setReturnedItems(returnedItems);
        
        if (totalItems > 0) {
            double returnRate = (double) returnedItems / totalItems * 100;
            statistics.setReturnRate(Math.round(returnRate * 100.0) / 100.0);
        } else {
            statistics.setReturnRate(0.0);
        }
        
        statistics.setLineStatistics(getLineStatistics(totalItems));
        statistics.setItemTypeStatistics(getItemTypeStatistics(totalItems));
        statistics.setMonthStatistics(getMonthStatistics());
        
        return statistics;
    }
    
    private List<StatisticsDTO.LineStatistics> getLineStatistics(long totalItems) {
        List<StatisticsDTO.LineStatistics> lineStats = new ArrayList<>();
        List<Object[]> counts = itemRepository.countBySubwayLine();
        
        Map<Long, Long> lineCountMap = new HashMap<>();
        for (Object[] obj : counts) {
            if (obj[0] != null) {
                lineCountMap.put(((Number) obj[0]).longValue(), ((Number) obj[1]).longValue());
            }
        }
        
        List<SubwayLine> lines = subwayLineRepository.findAll();
        for (SubwayLine line : lines) {
            StatisticsDTO.LineStatistics stat = new StatisticsDTO.LineStatistics();
            stat.setLineId(line.getId());
            stat.setLineName(line.getLineName());
            long count = lineCountMap.getOrDefault(line.getId(), 0L);
            stat.setCount(count);
            stat.setPercentage(totalItems > 0 ? Math.round((double) count / totalItems * 10000.0) / 100.0 : 0.0);
            lineStats.add(stat);
        }
        
        return lineStats;
    }
    
    private List<StatisticsDTO.ItemTypeStatistics> getItemTypeStatistics(long totalItems) {
        List<StatisticsDTO.ItemTypeStatistics> typeStats = new ArrayList<>();
        List<Object[]> counts = itemRepository.countByItemType();
        
        Map<Long, Long> typeCountMap = new HashMap<>();
        for (Object[] obj : counts) {
            if (obj[0] != null) {
                typeCountMap.put(((Number) obj[0]).longValue(), ((Number) obj[1]).longValue());
            }
        }
        
        List<ItemTypeCategory> types = itemTypeCategoryRepository.findAll();
        for (ItemTypeCategory type : types) {
            StatisticsDTO.ItemTypeStatistics stat = new StatisticsDTO.ItemTypeStatistics();
            stat.setTypeId(type.getId());
            stat.setTypeName(type.getTypeName());
            long count = typeCountMap.getOrDefault(type.getId(), 0L);
            stat.setCount(count);
            stat.setPercentage(totalItems > 0 ? Math.round((double) count / totalItems * 10000.0) / 100.0 : 0.0);
            typeStats.add(stat);
        }
        
        return typeStats;
    }
    
    private List<StatisticsDTO.MonthStatistics> getMonthStatistics() {
        List<StatisticsDTO.MonthStatistics> monthStats = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        for (int i = 5; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.now().minusMonths(i);
            LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            
            long total = itemRepository.countByCreatedAtBetween(start, end);
            long returned = itemRepository.countByStatusAndCreatedAtBetween(ItemStatus.RETURNED, start, end);
            
            StatisticsDTO.MonthStatistics stat = new StatisticsDTO.MonthStatistics();
            stat.setMonth(yearMonth.format(formatter));
            stat.setTotal(total);
            stat.setReturned(returned);
            monthStats.add(stat);
        }
        
        return monthStats;
    }
}
