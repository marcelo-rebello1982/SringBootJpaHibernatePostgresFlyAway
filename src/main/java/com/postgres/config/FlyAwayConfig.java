package com.postgres.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAspectJAutoProxy
@EntityScan(FlyAwayConfig.PACOTE)
@ComponentScan({ FlyAwayConfig.PACOTE })
@EnableJpaRepositories(basePackages = FlyAwayConfig.PACOTE)
@PropertySource("classpath:application_flyway.properties")
public class FlyAwayConfig {
    static final String PACOTE = "com.postgres.model";
}