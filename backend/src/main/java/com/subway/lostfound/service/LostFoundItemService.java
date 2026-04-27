package com.subway.lostfound.service;

import com.subway.lostfound.dto.LostFoundItemRequest;
import com.subway.lostfound.dto.LostFoundItemResponse;
import com.subway.lostfound.entity.ItemTypeCategory;
import com.subway.lostfound.entity.LostFoundItem;
import com.subway.lostfound.entity.SubwayLine;
import com.subway.lostfound.entity.User;
import com.subway.lostfound.entity.enums.ItemStatus;
import com.subway.lostfound.entity.enums.ItemType;
import com.subway.lostfound.repository.ItemTypeCategoryRepository;
import com.subway.lostfound.repository.LostFoundItemRepository;
import com.subway.lostfound.repository.SubwayLineRepository;
import com.subway.lostfound.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LostFoundItemService {
    
    @Autowired
    private LostFoundItemRepository itemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ItemTypeCategoryRepository itemTypeCategoryRepository;
    
    @Autowired
    private SubwayLineRepository subwayLineRepository;
    
    @Transactional
    public LostFoundItemResponse createItem(LostFoundItemRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        LostFoundItem item = new LostFoundItem();
        item.setItemType(request.getItemType());
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setItemTypeId(request.getItemTypeId());
        item.setSubwayLineId(request.getSubwayLineId());
        item.setStationName(request.getStationName());
        item.setLostFoundTime(request.getLostFoundTime());
        item.setContactName(request.getContactName());
        item.setContactPhone(request.getContactPhone());
        item.setContactEmail(request.getContactEmail());
        item.setImageUrls(request.getImageUrls());
        item.setPublisherId(userId);
        item.setStatus(ItemStatus.PENDING);
        item.setViewCount(0);
        
        item = itemRepository.save(item);
        return convertToResponse(item);
    }
    
    public LostFoundItemResponse getItemById(Long id) {
        LostFoundItem item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("记录不存在"));
        
        item.setViewCount(item.getViewCount() + 1);
        itemRepository.save(item);
        
        return convertToResponse(item);
    }
    
    public Page<LostFoundItemResponse> getApprovedItems(Pageable pageable) {
        return itemRepository.findByStatusOrderByCreatedAtDesc(ItemStatus.APPROVED, pageable)
                .map(this::convertToResponse);
    }
    
    public Page<LostFoundItemResponse> getApprovedItemsByType(ItemType itemType, Pageable pageable) {
        return itemRepository.findByStatusAndItemTypeOrderByCreatedAtDesc(ItemStatus.APPROVED, itemType, pageable)
                .map(this::convertToResponse);
    }
    
    public Page<LostFoundItemResponse> getUserItems(Long userId, Pageable pageable) {
        return itemRepository.findByPublisherIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::convertToResponse);
    }
    
    public Page<LostFoundItemResponse> getAllItems(ItemStatus status, ItemType itemType, 
                                                     Long subwayLineId, Long itemTypeId, 
                                                     String keyword, Pageable pageable) {
        Specification<LostFoundItem> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (itemType != null) {
                predicates.add(criteriaBuilder.equal(root.get("itemType"), itemType));
            }
            if (subwayLineId != null) {
                predicates.add(criteriaBuilder.equal(root.get("subwayLineId"), subwayLineId));
            }
            if (itemTypeId != null) {
                predicates.add(criteriaBuilder.equal(root.get("itemTypeId"), itemTypeId));
            }
            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("title"), "%" + keyword + "%"),
                        criteriaBuilder.like(root.get("description"), "%" + keyword + "%")
                ));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        return itemRepository.findAll(spec, pageable).map(this::convertToResponse);
    }
    
    @Transactional
    public LostFoundItemResponse updateItemStatus(Long id, ItemStatus status, String reason, Long adminId) {
        LostFoundItem item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("记录不存在"));
        
        item.setStatus(status);
        if (status == ItemStatus.REJECTED && reason != null) {
            item.setRejectReason(reason);
        }
        
        item = itemRepository.save(item);
        return convertToResponse(item);
    }
    
    @Transactional
    public void deleteItem(Long id, Long userId, boolean isAdmin) {
        LostFoundItem item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("记录不存在"));
        
        if (!isAdmin && !item.getPublisherId().equals(userId)) {
            throw new RuntimeException("没有权限删除此记录");
        }
        
        itemRepository.delete(item);
    }
    
    private LostFoundItemResponse convertToResponse(LostFoundItem item) {
        LostFoundItemResponse response = new LostFoundItemResponse();
        response.setId(item.getId());
        response.setItemType(item.getItemType());
        response.setTitle(item.getTitle());
        response.setDescription(item.getDescription());
        response.setItemTypeId(item.getItemTypeId());
        response.setSubwayLineId(item.getSubwayLineId());
        response.setStationName(item.getStationName());
        response.setLostFoundTime(item.getLostFoundTime());
        response.setContactName(item.getContactName());
        response.setContactPhone(item.getContactPhone());
        response.setContactEmail(item.getContactEmail());
        response.setImageUrls(item.getImageUrls());
        response.setPublisherId(item.getPublisherId());
        response.setStatus(item.getStatus());
        response.setRejectReason(item.getRejectReason());
        response.setAdminRemark(item.getAdminRemark());
        response.setViewCount(item.getViewCount());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());
        
        if (item.getItemTypeId() != null) {
            itemTypeCategoryRepository.findById(item.getItemTypeId()).ifPresent(type -> {
                response.setItemTypeName(type.getTypeName());
            });
        }
        
        if (item.getSubwayLineId() != null) {
            subwayLineRepository.findById(item.getSubwayLineId()).ifPresent(line -> {
                response.setSubwayLineName(line.getLineName());
            });
        }
        
        if (item.getPublisherId() != null) {
            userRepository.findById(item.getPublisherId()).ifPresent(user -> {
                response.setPublisherName(user.getRealName() != null ? user.getRealName() : user.getUsername());
            });
        }
        
        return response;
    }
}
