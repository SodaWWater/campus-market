package com.liminghan.market.service;

import com.liminghan.market.dto.LoginRequest;
import com.liminghan.market.dto.RegisterRequest;
import com.liminghan.market.vo.LoginResponseVO;

public interface AuthService {

    LoginResponseVO register(RegisterRequest request);

    LoginResponseVO login(LoginRequest request);
}
