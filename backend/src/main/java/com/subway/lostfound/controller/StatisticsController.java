package com.subway.lostfound.controller;

import com.subway.lostfound.dto.ApiResponse;
import com.subway.lostfound.dto.StatisticsDTO;
import com.subway.lostfound.entity.enums.ItemStatus;
import com.subway.lostfound.service.ExcelExportService;
import com.subway.lostfound.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/admin/statistics")
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    @Autowired
    private ExcelExportService excelExportService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    @GetMapping
    public ResponseEntity<ApiResponse<StatisticsDTO>> getStatistics() {
        StatisticsDTO statistics = statisticsService.getStatistics();
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    @GetMapping("/export/items")
    public ResponseEntity<byte[]> exportItems(@RequestParam(required = false) ItemStatus status) {
        try {
            byte[] excelData = excelExportService.exportItems(status);
            String fileName = "items_export_" + LocalDateTime.now().format(DATE_FORMATTER) + ".xlsx";
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelData);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/export/statistics")
    public ResponseEntity<byte[]> exportStatistics() {
        try {
            byte[] excelData = excelExportService.exportStatistics();
            String fileName = "statistics_export_" + LocalDateTime.now().format(DATE_FORMATTER) + ".xlsx";
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelData);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
