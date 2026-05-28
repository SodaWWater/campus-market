package com.liminghan.market.security;

import com.liminghan.market.entity.SysUser;
import com.liminghan.market.service.SysUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final SysUserService sysUserService;

    public CustomUserDetailsService(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserService.lambdaQuery().eq(SysUser::getUsername, username).one();
        if (user == null) {
            throw new UsernameNotFoundException("user not found");
        }
        return new SecurityUser(user);
    }
}
