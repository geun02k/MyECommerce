package com.myecommerce.MyECommerce.repository.member;

import com.myecommerce.MyECommerce.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 동일 전화번호 조회
    Optional<Member> findByTelephone(String telephone);
}
