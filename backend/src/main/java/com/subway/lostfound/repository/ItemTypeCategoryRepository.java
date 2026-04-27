package com.subway.lostfound.repository;

import com.subway.lostfound.entity.ItemTypeCategory;
import com.subway.lostfound.entity.enums.LineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemTypeCategoryRepository extends JpaRepository<ItemTypeCategory, Long>, JpaSpecificationExecutor<ItemTypeCategory> {
    
    List<ItemTypeCategory> findByStatusOrderBySortOrderAsc(LineStatus status);
}
