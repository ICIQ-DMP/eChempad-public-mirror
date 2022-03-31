/**
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad;

import org.ICIQ.eChempad.configurations.FileStorageProperties;
import org.hibernate.Session;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//@SpringBootApplication
// @SpringBootApplication is equivalent to:
@Configuration
@EnableAutoConfiguration
@ComponentScan

@EntityScan("org.ICIQ.eChempad")
@EnableConfigurationProperties({
		FileStorageProperties.class
})
public class EChempadApplication {

	public static void main(String[] args) {
		SpringApplication.run(EChempadApplication.class, args);
	}

}
