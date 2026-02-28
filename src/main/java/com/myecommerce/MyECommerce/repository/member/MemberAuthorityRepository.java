package com.myecommerce.MyECommerce.repository.member;

import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberAuthorityRepository extends JpaRepository<MemberAuthority, Long> {

    List<MemberAuthority> findByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);
}
