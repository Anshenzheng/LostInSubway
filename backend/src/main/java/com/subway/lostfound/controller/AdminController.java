package com.subway.lostfound.controller;

import com.subway.lostfound.dto.*;
import com.subway.lostfound.entity.enums.ClaimStatus;
import com.subway.lostfound.entity.enums.ItemStatus;
import com.subway.lostfound.entity.enums.ItemType;
import com.subway.lostfound.security.JwtUserDetailsService;
import com.subway.lostfound.service.ClaimService;
import com.subway.lostfound.service.LostFoundItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private LostFoundItemService itemService;
    
    @Autowired
    private ClaimService claimService;
    
    @Autowired
    private JwtUserDetailsService userDetailsService;
    
    @GetMapping("/items")
    public ResponseEntity<ApiResponse<PageResponse<LostFoundItemResponse>>> getAllItems(
            @RequestParam(required = false) ItemStatus status,
            @RequestParam(required = false) ItemType itemType,
            @RequestParam(required = false) Long subwayLineId,
            @RequestParam(required = false) Long itemTypeId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LostFoundItemResponse> items = itemService.getAllItems(
                status, itemType, subwayLineId, itemTypeId, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(items)));
    }
    
    @PutMapping("/items/{id}/approve")
    public ResponseEntity<ApiResponse<LostFoundItemResponse>> approveItem(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long adminId = getUserIdFromUserDetails(userDetails);
            LostFoundItemResponse response = itemService.updateItemStatus(id, ItemStatus.APPROVED, null, adminId);
            return ResponseEntity.ok(ApiResponse.success("审核通过", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/items/{id}/reject")
    public ResponseEntity<ApiResponse<LostFoundItemResponse>> rejectItem(
            @PathVariable Long id,
            @RequestBody(required = false) String reason,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long adminId = getUserIdFromUserDetails(userDetails);
            LostFoundItemResponse response = itemService.updateItemStatus(id, ItemStatus.REJECTED, reason, adminId);
            return ResponseEntity.ok(ApiResponse.success("已拒绝", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/items/{id}/returned")
    public ResponseEntity<ApiResponse<LostFoundItemResponse>> markAsReturned(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long adminId = getUserIdFromUserDetails(userDetails);
            LostFoundItemResponse response = itemService.updateItemStatus(id, ItemStatus.RETURNED, null, adminId);
            return ResponseEntity.ok(ApiResponse.success("已标记为已归还", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long adminId = getUserIdFromUserDetails(userDetails);
            itemService.deleteItem(id, adminId, true);
            return ResponseEntity.ok(ApiResponse.success("删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/claims")
    public ResponseEntity<ApiResponse<PageResponse<ClaimResponse>>> getAllClaims(
            @RequestParam(required = false) ClaimStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClaimResponse> claims = claimService.getAllClaims(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(claims)));
    }
    
    @PutMapping("/claims/{id}/approve")
    public ResponseEntity<ApiResponse<ClaimResponse>> approveClaim(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long adminId = getUserIdFromUserDetails(userDetails);
            ClaimResponse response = claimService.approveClaim(id, adminId);
            return ResponseEntity.ok(ApiResponse.success("认领通过", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/claims/{id}/reject")
    public ResponseEntity<ApiResponse<ClaimResponse>> rejectClaim(
            @PathVariable Long id,
            @RequestBody(required = false) String reason,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long adminId = getUserIdFromUserDetails(userDetails);
            ClaimResponse response = claimService.rejectClaim(id, reason, adminId);
            return ResponseEntity.ok(ApiResponse.success("认领已拒绝", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        return userDetailsService.loadUserEntityByUsername(userDetails.getUsername()).getId();
    }
}
