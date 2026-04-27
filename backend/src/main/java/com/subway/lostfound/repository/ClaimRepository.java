package com.subway.lostfound.repository;

import com.subway.lostfound.entity.Claim;
import com.subway.lostfound.entity.enums.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long>, JpaSpecificationExecutor<Claim> {
    
    Page<Claim> findByClaimerIdOrderByCreatedAtDesc(Long claimerId, Pageable pageable);
    
    Page<Claim> findByStatusOrderByCreatedAtDesc(ClaimStatus status, Pageable pageable);
    
    List<Claim> findByItemIdAndStatus(Long itemId, ClaimStatus status);
    
    Optional<Claim> findByItemIdAndClaimerId(Long itemId, Long claimerId);
    
    Page<Claim> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
