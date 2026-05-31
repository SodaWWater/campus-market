package com.liminghan.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String phone;

    private String avatarUrl;

    private String school;

    private String major;

    private String grade;

    private String bio;

    private Integer creditScore;

    private String role;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
