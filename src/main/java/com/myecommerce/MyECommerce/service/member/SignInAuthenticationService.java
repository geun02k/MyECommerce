package com.myecommerce.MyECommerce.service.member;

import com.myecommerce.MyECommerce.exception.MemberException;
import com.myecommerce.MyECommerce.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.myecommerce.MyECommerce.exception.errorcode.MemberErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SignInAuthenticationService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * 사용자인증
     * @param username the username identifying the user whose data is required.
     * @return UserDetails 사용자정보
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUserIdAndDelYn(username, 'N')
                .orElseThrow(() -> new MemberException(USER_NOT_FOUND));
    }

}
