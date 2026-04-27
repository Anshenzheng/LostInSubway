package com.subway.lostfound.controller;

import com.subway.lostfound.dto.ApiResponse;
import com.subway.lostfound.dto.ClaimRequest;
import com.subway.lostfound.dto.ClaimResponse;
import com.subway.lostfound.dto.PageResponse;
import com.subway.lostfound.security.JwtUserDetailsService;
import com.subway.lostfound.service.ClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {
    
    @Autowired
    private ClaimService claimService;
    
    @Autowired
    private JwtUserDetailsService userDetailsService;
    
    @PostMapping
    @PreAuthorize("hasRole('PASSENGER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClaimResponse>> createClaim(
            @Valid @RequestBody ClaimRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userId = getUserIdFromUserDetails(userDetails);
            ClaimResponse response = claimService.createClaim(request, userId);
            return ResponseEntity.ok(ApiResponse.success("认领申请提交成功", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PASSENGER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClaimResponse>> getClaimById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            ClaimResponse response = claimService.getClaimById(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/my")
    @PreAuthorize("hasRole('PASSENGER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<ClaimResponse>>> getMyClaims(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        Pageable pageable = PageRequest.of(page, size);
        Page<ClaimResponse> claims = claimService.getUserClaims(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(claims)));
    }
    
    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        return userDetailsService.loadUserEntityByUsername(userDetails.getUsername()).getId();
    }
}
