/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.zkoss.spring.config.ZkScopesConfigurer;

@Import(ZkScopesConfigurer.class)
@SpringBootApplication
// Scan packages to look for jpaRepositories interfaces where we need to inject dependencies
@EnableJpaRepositories(basePackages = {
		"org.ICIQ.eChempad.repositories"
})
public class EChempadApplication {

	public static void main(String[] args) {
		SpringApplication.run(EChempadApplication.class, args);
	}

}
