package com.liminghan.market.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liminghan.market.entity.SysUser;
import com.liminghan.market.mapper.SysUserMapper;
import com.liminghan.market.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
}
