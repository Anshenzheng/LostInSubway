package com.subway.lostfound.controller;

import com.subway.lostfound.dto.ApiResponse;
import com.subway.lostfound.dto.LostFoundItemResponse;
import com.subway.lostfound.dto.PageResponse;
import com.subway.lostfound.entity.ItemTypeCategory;
import com.subway.lostfound.entity.SubwayLine;
import com.subway.lostfound.entity.enums.ItemType;
import com.subway.lostfound.repository.ItemTypeCategoryRepository;
import com.subway.lostfound.repository.SubwayLineRepository;
import com.subway.lostfound.service.LostFoundItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {
    
    @Autowired
    private LostFoundItemService itemService;
    
    @Autowired
    private SubwayLineRepository subwayLineRepository;
    
    @Autowired
    private ItemTypeCategoryRepository itemTypeCategoryRepository;
    
    @GetMapping("/items")
    public ResponseEntity<ApiResponse<PageResponse<LostFoundItemResponse>>> getApprovedItems(
            @RequestParam(required = false) ItemType itemType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LostFoundItemResponse> items;
        if (itemType != null) {
            items = itemService.getApprovedItemsByType(itemType, pageable);
        } else {
            items = itemService.getApprovedItems(pageable);
        }
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(items)));
    }
    
    @GetMapping("/items/{id}")
    public ResponseEntity<ApiResponse<LostFoundItemResponse>> getItemById(@PathVariable Long id) {
        try {
            LostFoundItemResponse response = itemService.getItemById(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/subway-lines")
    public ResponseEntity<ApiResponse<List<SubwayLine>>> getSubwayLines() {
        List<SubwayLine> lines = subwayLineRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(lines));
    }
    
    @GetMapping("/item-types")
    public ResponseEntity<ApiResponse<List<ItemTypeCategory>>> getItemTypes() {
        List<ItemTypeCategory> types = itemTypeCategoryRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(types));
    }
}
