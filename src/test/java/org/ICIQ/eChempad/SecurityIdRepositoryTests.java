package org.ICIQ.eChempad;

import org.ICIQ.eChempad.entities.SecurityId;
import org.ICIQ.eChempad.repositories.manualRepositories.SecurityIdRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest  // Test spring boot JPA modules
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // Use the usual DB (postgresql)
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver",
        "spring.datasource.url=jdbc:tc:postgresql:13.2-alpine:///eChempad",  // Use docker to bootstrap postgresql
        "spring.flyway.enabled=false",  // Deactivate flyway (not used currently)
        "spring.jpa.hibernate.ddl-auto=create-drop"  // Create-drop schema on each test
})  // Test Container
class SecurityIdRepositoryTests {

    SecurityIdRepository securityIdRepository;

    @Test
    void securityIdIsSaved() {
        // Create sid in memory
        SecurityId sid = new SecurityId(23L, true, "eChempad@iciq.es");

        // Persist it to the DB
        securityIdRepository.save(sid);

        // Retrieve it again
        SecurityId sidDB = securityIdRepository.getById(sid.getId());

        assertEquals(sidDB, sid);
    }
}
