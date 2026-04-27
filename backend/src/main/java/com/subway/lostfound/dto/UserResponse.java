package com.subway.lostfound.dto;

import com.subway.lostfound.entity.enums.UserRole;
import com.subway.lostfound.entity.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
