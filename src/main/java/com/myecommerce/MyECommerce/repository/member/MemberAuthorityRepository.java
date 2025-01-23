package com.myecommerce.MyECommerce.repository.member;

import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAuthorityRepository extends JpaRepository<MemberAuthority, Long> {
}
