package com.liminghan.market.security;

import com.liminghan.market.common.BusinessException;
import com.liminghan.market.common.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityContextUtil {

    private SecurityContextUtil() {
    }

    public static SecurityUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser securityUser)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "login required");
        }
        return securityUser;
    }
}
