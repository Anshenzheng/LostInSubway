package com.subway.lostfound.controller;

import com.subway.lostfound.dto.ApiResponse;
import com.subway.lostfound.dto.JwtResponse;
import com.subway.lostfound.dto.LoginRequest;
import com.subway.lostfound.dto.RegisterRequest;
import com.subway.lostfound.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            JwtResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success("登录成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<JwtResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            JwtResponse response = authService.register(request);
            return ResponseEntity.ok(ApiResponse.success("注册成功", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
