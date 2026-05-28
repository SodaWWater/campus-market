package com.liminghan.market.service.impl;

import com.liminghan.market.common.BusinessException;
import com.liminghan.market.common.ErrorCode;
import com.liminghan.market.dto.LoginRequest;
import com.liminghan.market.dto.RegisterRequest;
import com.liminghan.market.entity.SysUser;
import com.liminghan.market.security.JwtUtil;
import com.liminghan.market.service.AuthService;
import com.liminghan.market.service.SysUserService;
import com.liminghan.market.vo.LoginResponseVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Set<String> ROLES = Set.of("USER", "ADMIN");

    private final SysUserService sysUserService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(SysUserService sysUserService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.sysUserService = sysUserService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResponseVO register(RegisterRequest request) {
        long count = sysUserService.lambdaQuery().eq(SysUser::getUsername, request.getUsername()).count();
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "username already exists");
        }
        String role = request.getRole() == null || request.getRole().isBlank() ? "USER" : request.getRole().toUpperCase();
        if (!ROLES.contains(role)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "role must be USER or ADMIN");
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        sysUserService.save(user);
        return buildLoginResponse(user);
    }

    @Override
    public LoginResponseVO login(LoginRequest request) {
        SysUser user = sysUserService.lambdaQuery().eq(SysUser::getUsername, request.getUsername()).one();
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "username or password is incorrect");
        }
        return buildLoginResponse(user);
    }

    private LoginResponseVO buildLoginResponse(SysUser user) {
        LoginResponseVO response = new LoginResponseVO();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setToken(jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole()));
        return response;
    }
}
