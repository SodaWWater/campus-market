package com.liminghan.market.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {

    private String nickname;

    private String phone;

    private String avatarUrl;

    private String school;

    private String major;

    private String grade;

    private String bio;
}
