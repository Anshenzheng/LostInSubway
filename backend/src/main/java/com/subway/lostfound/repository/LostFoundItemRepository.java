package com.subway.lostfound.repository;

import com.subway.lostfound.entity.LostFoundItem;
import com.subway.lostfound.entity.enums.ItemStatus;
import com.subway.lostfound.entity.enums.ItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LostFoundItemRepository extends JpaRepository<LostFoundItem, Long>, JpaSpecificationExecutor<LostFoundItem> {
    
    Page<LostFoundItem> findByStatusOrderByCreatedAtDesc(ItemStatus status, Pageable pageable);
    
    Page<LostFoundItem> findByStatusAndItemTypeOrderByCreatedAtDesc(ItemStatus status, ItemType itemType, Pageable pageable);
    
    Page<LostFoundItem> findByPublisherIdOrderByCreatedAtDesc(Long publisherId, Pageable pageable);
    
    List<LostFoundItem> findByStatus(ItemStatus status);
    
    long countByStatus(ItemStatus status);
    
    @Query("SELECT COUNT(l) FROM LostFoundItem l WHERE l.createdAt >= :startDate AND l.createdAt < :endDate")
    long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(l) FROM LostFoundItem l WHERE l.status = :status AND l.createdAt >= :startDate AND l.createdAt < :endDate")
    long countByStatusAndCreatedAtBetween(@Param("status") ItemStatus status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT l.subwayLineId, COUNT(l) FROM LostFoundItem l GROUP BY l.subwayLineId")
    List<Object[]> countBySubwayLine();
    
    @Query("SELECT l.itemTypeId, COUNT(l) FROM LostFoundItem l GROUP BY l.itemTypeId")
    List<Object[]> countByItemType();
    
    @Query("SELECT l.itemType, COUNT(l) FROM LostFoundItem l WHERE l.status = :status GROUP BY l.itemType")
    List<Object[]> countByItemTypeAndStatus(@Param("status") ItemStatus status);
}
