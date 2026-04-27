package com.subway.lostfound.service;

import com.subway.lostfound.dto.StatisticsDTO;
import com.subway.lostfound.entity.LostFoundItem;
import com.subway.lostfound.entity.enums.ItemStatus;
import com.subway.lostfound.repository.LostFoundItemRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {
    
    @Autowired
    private LostFoundItemRepository itemRepository;
    
    @Autowired
    private StatisticsService statisticsService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public byte[] exportItems(ItemStatus status) throws IOException {
        List<LostFoundItem> items;
        if (status != null) {
            items = itemRepository.findByStatus(status);
        } else {
            items = itemRepository.findAll();
        }
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("失物招领记录");
            
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            
            String[] headers = {"ID", "类型", "标题", "描述", "状态", "发布时间", "联系电话", "联系邮箱", "浏览次数"};
            
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            int rowNum = 1;
            for (LostFoundItem item : items) {
                Row row = sheet.createRow(rowNum++);
                createCell(row, 0, item.getId(), dataStyle);
                createCell(row, 1, item.getItemType().name(), dataStyle);
                createCell(row, 2, item.getTitle(), dataStyle);
                createCell(row, 3, item.getDescription().length() > 100 ? item.getDescription().substring(0, 100) + "..." : item.getDescription(), dataStyle);
                createCell(row, 4, item.getStatus().name(), dataStyle);
                createCell(row, 5, item.getCreatedAt() != null ? item.getCreatedAt().format(DATE_FORMATTER) : "", dataStyle);
                createCell(row, 6, item.getContactPhone(), dataStyle);
                createCell(row, 7, item.getContactEmail(), dataStyle);
                createCell(row, 8, item.getViewCount(), dataStyle);
            }
            
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }
    
    public byte[] exportStatistics() throws IOException {
        StatisticsDTO statistics = statisticsService.getStatistics();
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet summarySheet = workbook.createSheet("统计概览");
            Sheet lineSheet = workbook.createSheet("线路分布");
            Sheet typeSheet = workbook.createSheet("类型分布");
            Sheet monthSheet = workbook.createSheet("月度趋势");
            
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            
            // 统计概览
            String[] summaryHeaders = {"指标", "数值"};
            Object[][] summaryData = {
                    {"总记录数", statistics.getTotalItems()},
                    {"待审核", statistics.getPendingItems()},
                    {"已通过", statistics.getApprovedItems()},
                    {"已拒绝", statistics.getRejectedItems()},
                    {"已认领", statistics.getClaimedItems()},
                    {"已归还", statistics.getReturnedItems()},
                    {"归还率(%)", statistics.getReturnRate()}
            };
            populateSheet(summarySheet, summaryHeaders, summaryData, headerStyle, dataStyle);
            
            // 线路分布
            if (statistics.getLineStatistics() != null && !statistics.getLineStatistics().isEmpty()) {
                String[] lineHeaders = {"线路ID", "线路名称", "数量", "占比(%)"};
                int rowNum = 0;
                Row headerRow = lineSheet.createRow(rowNum++);
                for (int i = 0; i < lineHeaders.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(lineHeaders[i]);
                    cell.setCellStyle(headerStyle);
                }
                for (StatisticsDTO.LineStatistics stat : statistics.getLineStatistics()) {
                    Row row = lineSheet.createRow(rowNum++);
                    createCell(row, 0, stat.getLineId(), dataStyle);
                    createCell(row, 1, stat.getLineName(), dataStyle);
                    createCell(row, 2, stat.getCount(), dataStyle);
                    createCell(row, 3, stat.getPercentage(), dataStyle);
                }
                for (int i = 0; i < lineHeaders.length; i++) {
                    lineSheet.autoSizeColumn(i);
                }
            }
            
            // 类型分布
            if (statistics.getItemTypeStatistics() != null && !statistics.getItemTypeStatistics().isEmpty()) {
                String[] typeHeaders = {"类型ID", "类型名称", "数量", "占比(%)"};
                int rowNum = 0;
                Row headerRow = typeSheet.createRow(rowNum++);
                for (int i = 0; i < typeHeaders.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(typeHeaders[i]);
                    cell.setCellStyle(headerStyle);
                }
                for (StatisticsDTO.ItemTypeStatistics stat : statistics.getItemTypeStatistics()) {
                    Row row = typeSheet.createRow(rowNum++);
                    createCell(row, 0, stat.getTypeId(), dataStyle);
                    createCell(row, 1, stat.getTypeName(), dataStyle);
                    createCell(row, 2, stat.getCount(), dataStyle);
                    createCell(row, 3, stat.getPercentage(), dataStyle);
                }
                for (int i = 0; i < typeHeaders.length; i++) {
                    typeSheet.autoSizeColumn(i);
                }
            }
            
            // 月度趋势
            if (statistics.getMonthStatistics() != null && !statistics.getMonthStatistics().isEmpty()) {
                String[] monthHeaders = {"月份", "总数", "已归还"};
                int rowNum = 0;
                Row headerRow = monthSheet.createRow(rowNum++);
                for (int i = 0; i < monthHeaders.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(monthHeaders[i]);
                    cell.setCellStyle(headerStyle);
                }
                for (StatisticsDTO.MonthStatistics stat : statistics.getMonthStatistics()) {
                    Row row = monthSheet.createRow(rowNum++);
                    createCell(row, 0, stat.getMonth(), dataStyle);
                    createCell(row, 1, stat.getTotal(), dataStyle);
                    createCell(row, 2, stat.getReturned(), dataStyle);
                }
                for (int i = 0; i < monthHeaders.length; i++) {
                    monthSheet.autoSizeColumn(i);
                }
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }
    
    private void populateSheet(Sheet sheet, String[] headers, Object[][] data, 
                                CellStyle headerStyle, CellStyle dataStyle) {
        int rowNum = 0;
        Row headerRow = sheet.createRow(rowNum++);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        for (Object[] rowData : data) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < rowData.length; i++) {
                createCell(row, i, rowData[i], dataStyle);
            }
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value != null) {
            cell.setCellValue(value.toString());
        }
        cell.setCellStyle(style);
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}
