package com.liminghan.market.controller;

import com.liminghan.market.common.BusinessException;
import com.liminghan.market.common.ErrorCode;
import com.liminghan.market.common.Result;
import com.liminghan.market.dto.ProfileUpdateRequest;
import com.liminghan.market.entity.SysUser;
import com.liminghan.market.security.SecurityContextUtil;
import com.liminghan.market.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final SysUserService sysUserService;

    public UserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @Operation(summary = "Current user profile")
    @GetMapping("/profile")
    public Result<SysUser> profile() {
        return Result.success(maskPassword(getRequiredUser(SecurityContextUtil.currentUser().getUserId())));
    }

    @Operation(summary = "Update current user profile")
    @PutMapping("/profile")
    public Result<SysUser> updateProfile(@RequestBody ProfileUpdateRequest request) {
        SysUser user = getRequiredUser(SecurityContextUtil.currentUser().getUserId());
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getSchool() != null) {
            user.setSchool(request.getSchool());
        }
        if (request.getMajor() != null) {
            user.setMajor(request.getMajor());
        }
        if (request.getGrade() != null) {
            user.setGrade(request.getGrade());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        user.setUpdatedAt(LocalDateTime.now());
        sysUserService.updateById(user);
        return Result.success(maskPassword(user));
    }

    @Operation(summary = "Public user profile")
    @GetMapping("/{id}")
    public Result<SysUser> publicProfile(@PathVariable Long id) {
        SysUser user = getRequiredUser(id);
        user.setPhone(null);
        return Result.success(maskPassword(user));
    }

    private SysUser getRequiredUser(Long id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "user not found");
        }
        return user;
    }

    private SysUser maskPassword(SysUser user) {
        user.setPassword(null);
        return user;
    }
}
