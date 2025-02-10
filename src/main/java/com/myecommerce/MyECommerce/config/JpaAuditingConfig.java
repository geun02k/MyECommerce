package com.myecommerce.MyECommerce.config;

import com.myecommerce.MyECommerce.security.SecurityAuditorAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

    // DB 생성자, 수정자 자동입력을 위해 빈생성&등록.
    @Bean
    public AuditorAware<Long> auditorAware() {
        return new SecurityAuditorAware();
    }
}
