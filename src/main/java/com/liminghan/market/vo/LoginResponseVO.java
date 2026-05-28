package com.liminghan.market.vo;

import lombok.Data;

@Data
public class LoginResponseVO {

    private String token;

    private Long userId;

    private String username;

    private String role;
}
