package com.subway.lostfound.dto;

import com.subway.lostfound.entity.enums.ItemType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class LostFoundItemRequest {
    
    @NotNull(message = "类型不能为空")
    private ItemType itemType;
    
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    private String title;
    
    @NotBlank(message = "描述不能为空")
    @Size(max = 2000, message = "描述长度不能超过2000个字符")
    private String description;
    
    private Long itemTypeId;
    
    private Long subwayLineId;
    
    @Size(max = 100, message = "站点名称长度不能超过100个字符")
    private String stationName;
    
    private LocalDateTime lostFoundTime;
    
    @Size(max = 50, message = "联系人姓名长度不能超过50个字符")
    private String contactName;
    
    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    private String contactPhone;
    
    @Size(max = 100, message = "联系邮箱长度不能超过100个字符")
    private String contactEmail;
    
    private String imageUrls;
}
