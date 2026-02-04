package com.myecommerce.MyECommerce.repository.member;

import com.myecommerce.MyECommerce.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 동일 전화번호 조회
    Optional<Member> findByTelephone(String telephone);

    // 사용자ID에 대한 회원정보 조회
    Optional<Member> findByUserIdAndDelYn(String userId, Character delYn);
}
