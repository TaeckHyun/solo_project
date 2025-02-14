package com.springboot.utils;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CheckValidator {
    // 로그인한 사용자와 작성자 동일한지 검증 메서드
    public boolean checkOwner(long ownerId, long principalOwnerId) {
        if (principalOwnerId != ownerId) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_OWNER);
        }
        return true;
    }

    // 어드민 검증 메서드
    public boolean checkAdmin() {
        // 인증된 객체를 불러옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getAuthorities().stream().anyMatch(
                grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN")
        );
    }

    // find 에서 쓸 권리자 이거나 작성자 인지 검증하는 메서드
    public void checkAdminAndCheckOwner(long ownerId, long principalOwnerId) {
        if (!checkOwner(ownerId, principalOwnerId) && !checkAdmin()) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }
    }
}
