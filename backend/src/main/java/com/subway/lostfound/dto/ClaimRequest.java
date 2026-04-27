package com.subway.lostfound.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ClaimRequest {
    
    @NotNull(message = "失物招领ID不能为空")
    private Long itemId;
    
    @NotBlank(message = "认领理由不能为空")
    @Size(max = 2000, message = "认领理由长度不能超过2000个字符")
    private String claimReason;
    
    private String proofImages;
}
