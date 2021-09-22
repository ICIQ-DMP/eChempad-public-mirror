package org.ICIQ.eChempad;

import org.hibernate.Session;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("org.ICIQ.eChempad")
public class EChempadApplication {

	public static void main(String[] args) {
		SpringApplication.run(EChempadApplication.class, args);
	}
	// instanciar classe interna de test.
	// grabar element fer crida (crear usuari nou)
	// comprovar pgadmin canvis a la BD

}
