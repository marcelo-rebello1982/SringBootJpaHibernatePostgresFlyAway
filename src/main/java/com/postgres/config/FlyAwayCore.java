package com.postgres.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EnableAspectJAutoProxy
@EntityScan(FlyAwayCore.PACOTE)
@ComponentScan({ FlyAwayCore.PACOTE })
@EnableJpaRepositories(basePackages = FlyAwayCore.PACOTE)
@PropertySource("classpath:application_core.properties")
public class FlyAwayCore {
    static final String PACOTE = "com.postgres.model";
}