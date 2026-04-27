package com.subway.lostfound.service;

import com.subway.lostfound.dto.ClaimRequest;
import com.subway.lostfound.dto.ClaimResponse;
import com.subway.lostfound.entity.Claim;
import com.subway.lostfound.entity.LostFoundItem;
import com.subway.lostfound.entity.User;
import com.subway.lostfound.entity.enums.ClaimStatus;
import com.subway.lostfound.entity.enums.ItemStatus;
import com.subway.lostfound.repository.ClaimRepository;
import com.subway.lostfound.repository.LostFoundItemRepository;
import com.subway.lostfound.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClaimService {
    
    @Autowired
    private ClaimRepository claimRepository;
    
    @Autowired
    private LostFoundItemRepository itemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public ClaimResponse createClaim(ClaimRequest request, Long userId) {
        LostFoundItem item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("失物招领记录不存在"));
        
        if (item.getStatus() != ItemStatus.APPROVED) {
            throw new RuntimeException("该记录不可认领");
        }
        
        Optional<Claim> existingClaim = claimRepository.findByItemIdAndClaimerId(request.getItemId(), userId);
        if (existingClaim.isPresent()) {
            throw new RuntimeException("您已提交过认领申请");
        }
        
        List<Claim> pendingClaims = claimRepository.findByItemIdAndStatus(request.getItemId(), ClaimStatus.PENDING);
        if (!pendingClaims.isEmpty()) {
            throw new RuntimeException("该物品已有待审核的认领申请");
        }
        
        Claim claim = new Claim();
        claim.setItemId(request.getItemId());
        claim.setClaimerId(userId);
        claim.setClaimReason(request.getClaimReason());
        claim.setProofImages(request.getProofImages());
        claim.setStatus(ClaimStatus.PENDING);
        
        claim = claimRepository.save(claim);
        return convertToResponse(claim);
    }
    
    public ClaimResponse getClaimById(Long id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("认领记录不存在"));
        return convertToResponse(claim);
    }
    
    public Page<ClaimResponse> getUserClaims(Long userId, Pageable pageable) {
        return claimRepository.findByClaimerIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::convertToResponse);
    }
    
    public Page<ClaimResponse> getAllClaims(ClaimStatus status, Pageable pageable) {
        if (status != null) {
            return claimRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
                    .map(this::convertToResponse);
        }
        return claimRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::convertToResponse);
    }
    
    @Transactional
    public ClaimResponse approveClaim(Long id, Long adminId) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("认领记录不存在"));
        
        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new RuntimeException("认领申请已处理");
        }
        
        claim.setStatus(ClaimStatus.APPROVED);
        claim = claimRepository.save(claim);
        
        LostFoundItem item = itemRepository.findById(claim.getItemId())
                .orElseThrow(() -> new RuntimeException("失物招领记录不存在"));
        item.setStatus(ItemStatus.CLAIMED);
        itemRepository.save(item);
        
        return convertToResponse(claim);
    }
    
    @Transactional
    public ClaimResponse rejectClaim(Long id, String reason, Long adminId) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("认领记录不存在"));
        
        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new RuntimeException("认领申请已处理");
        }
        
        claim.setStatus(ClaimStatus.REJECTED);
        claim.setRejectReason(reason);
        claim = claimRepository.save(claim);
        
        return convertToResponse(claim);
    }
    
    private ClaimResponse convertToResponse(Claim claim) {
        ClaimResponse response = new ClaimResponse();
        response.setId(claim.getId());
        response.setItemId(claim.getItemId());
        response.setClaimerId(claim.getClaimerId());
        response.setClaimReason(claim.getClaimReason());
        response.setProofImages(claim.getProofImages());
        response.setStatus(claim.getStatus());
        response.setRejectReason(claim.getRejectReason());
        response.setAdminRemark(claim.getAdminRemark());
        response.setCreatedAt(claim.getCreatedAt());
        response.setUpdatedAt(claim.getUpdatedAt());
        
        if (claim.getItemId() != null) {
            itemRepository.findById(claim.getItemId()).ifPresent(item -> {
                response.setItemTitle(item.getTitle());
            });
        }
        
        if (claim.getClaimerId() != null) {
            userRepository.findById(claim.getClaimerId()).ifPresent(user -> {
                response.setClaimerName(user.getRealName() != null ? user.getRealName() : user.getUsername());
            });
        }
        
        return response;
    }
}
