package com.postgres.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {
		"com.postgres.model"})
public class SpringJpaPostgresHibernateApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringJpaPostgresHibernateApplication.class, args);
	}

}
