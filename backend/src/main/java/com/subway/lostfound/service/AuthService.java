package com.subway.lostfound.service;

import com.subway.lostfound.dto.JwtResponse;
import com.subway.lostfound.dto.LoginRequest;
import com.subway.lostfound.dto.RegisterRequest;
import com.subway.lostfound.entity.User;
import com.subway.lostfound.entity.enums.UserRole;
import com.subway.lostfound.entity.enums.UserStatus;
import com.subway.lostfound.repository.UserRepository;
import com.subway.lostfound.security.JwtTokenUtil;
import com.subway.lostfound.security.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private JwtUserDetailsService userDetailsService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    public JwtResponse login(LoginRequest request) throws Exception {
        authenticate(request.getUsername(), request.getPassword());
        
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        
        User user = userDetailsService.loadUserEntityByUsername(request.getUsername());
        
        return new JwtResponse(token, user.getId(), user.getUsername(), user.getRealName(), user.getRole().name());
    }
    
    @Transactional
    public JwtResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(UserRole.PASSENGER);
        user.setStatus(UserStatus.ACTIVE);
        
        user = userRepository.save(user);
        
        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        
        return new JwtResponse(token, user.getId(), user.getUsername(), user.getRealName(), user.getRole().name());
    }
    
    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("用户已被禁用", e);
        } catch (BadCredentialsException e) {
            throw new Exception("用户名或密码错误", e);
        }
    }
}
