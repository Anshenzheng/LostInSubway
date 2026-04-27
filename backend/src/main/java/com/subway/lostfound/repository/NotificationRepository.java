package com.subway.lostfound.repository;

import com.subway.lostfound.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    List<Notification> findByUserIdAndIsReadOrderByCreatedAtDesc(Long userId, Boolean isRead);
    
    long countByUserIdAndIsRead(Long userId, Boolean isRead);
}
