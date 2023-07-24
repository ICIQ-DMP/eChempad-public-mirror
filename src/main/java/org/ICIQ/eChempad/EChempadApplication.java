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
// @SpringBootApplication This annotation includes three others, but is not well recognized in intelliJ... Substitute
// this annotation with the component scan annotation and put the packages that need to be recognized by the IDE.
// https://stackoverflow.com/questions/26889970/intellij-incorrectly-saying-no-beans-of-type-found-for-autowired-repository
// I could not make the change, changing this by the three equivalent annotation breaks the runtime.
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
