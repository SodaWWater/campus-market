package com.liminghan.market.controller;

import com.liminghan.market.common.Result;
import com.liminghan.market.dto.LoginRequest;
import com.liminghan.market.dto.RegisterRequest;
import com.liminghan.market.service.AuthService;
import com.liminghan.market.vo.LoginResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register user")
    @PostMapping("/register")
    public Result<LoginResponseVO> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    @Operation(summary = "Login user")
    @PostMapping("/login")
    public Result<LoginResponseVO> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }
}
