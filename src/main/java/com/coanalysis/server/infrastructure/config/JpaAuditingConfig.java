package com.coanalysis.server.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 설정
 * @CreatedDate, @LastModifiedDate 등의 어노테이션이 동작하도록 활성화
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
