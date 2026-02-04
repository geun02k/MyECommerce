package com.myecommerce.MyECommerce.integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@TestConfiguration
@EnableJpaAuditing
public class TestAuditingConfig {

    @Bean
    public AuditorAware<Long> auditorAware() {
        return () -> Optional.of(1L);
    }

}
