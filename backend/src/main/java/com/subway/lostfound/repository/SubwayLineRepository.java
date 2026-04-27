package com.subway.lostfound.repository;

import com.subway.lostfound.entity.SubwayLine;
import com.subway.lostfound.entity.enums.LineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubwayLineRepository extends JpaRepository<SubwayLine, Long>, JpaSpecificationExecutor<SubwayLine> {
    
    List<SubwayLine> findByStatusOrderBySortOrderAsc(LineStatus status);
}
