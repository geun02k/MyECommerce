package com.myecommerce.MyECommerce.security;

import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.exception.SpringSecurityException;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static com.myecommerce.MyECommerce.exception.errorcode.SpringSecurityErrorCode.INVALID_TOKEN;

// DB 생성자, 수정자 자동입력을 위해 구현필요.
public class SecurityAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if(null == authentication || !authentication.isAuthenticated()) {
            throw new SpringSecurityException(INVALID_TOKEN);
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Member)){
            return Optional.empty();
        }

        final Member loginUser = (Member) principal;
        return Optional.of(loginUser.getId());
    }

}
