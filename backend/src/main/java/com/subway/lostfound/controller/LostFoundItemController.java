package com.subway.lostfound.controller;

import com.subway.lostfound.dto.ApiResponse;
import com.subway.lostfound.dto.LostFoundItemRequest;
import com.subway.lostfound.dto.LostFoundItemResponse;
import com.subway.lostfound.dto.PageResponse;
import com.subway.lostfound.entity.enums.ItemType;
import com.subway.lostfound.security.JwtUserDetailsService;
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

import javax.validation.Valid;

@RestController
@RequestMapping("/api/items")
public class LostFoundItemController {
    
    @Autowired
    private LostFoundItemService itemService;
    
    @Autowired
    private JwtUserDetailsService userDetailsService;
    
    @PostMapping
    @PreAuthorize("hasRole('PASSENGER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LostFoundItemResponse>> createItem(
            @Valid @RequestBody LostFoundItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        LostFoundItemResponse response = itemService.createItem(request, userId);
        return ResponseEntity.ok(ApiResponse.success("发布成功", response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LostFoundItemResponse>> getItemById(@PathVariable Long id) {
        try {
            LostFoundItemResponse response = itemService.getItemById(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/my")
    @PreAuthorize("hasRole('PASSENGER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<LostFoundItemResponse>>> getMyItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        Pageable pageable = PageRequest.of(page, size);
        Page<LostFoundItemResponse> items = itemService.getUserItems(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(items)));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PASSENGER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteItem(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userId = getUserIdFromUserDetails(userDetails);
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            itemService.deleteItem(id, userId, isAdmin);
            return ResponseEntity.ok(ApiResponse.success("删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        return userDetailsService.loadUserEntityByUsername(userDetails.getUsername()).getId();
    }
}
