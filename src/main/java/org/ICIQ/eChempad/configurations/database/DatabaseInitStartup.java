/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.database;

import org.ICIQ.eChempad.entities.genericJPAEntities.Authority;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.ICIQ.eChempad.entities.genericJPAEntities.Researcher;
import org.ICIQ.eChempad.entities.SecurityId;
import org.ICIQ.eChempad.repositories.SecurityIdRepository;
import org.ICIQ.eChempad.services.genericJPAServices.JournalService;
import org.ICIQ.eChempad.services.genericJPAServices.ResearcherService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.logging.Logger;

/**
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 10/10/2022
 *
 * This class contains the method
 * {@code public void onApplicationEvent(final @NotNull ApplicationReadyEvent event)}
 * which is executed after the application is "ready". That happens after all the initializations, but before
 * accepting traffic. In here we can implement ways to load data to the database at the start of the application.
 */
@Component
public class DatabaseInitStartup implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * We use our {@code ResearcherService} to load users to check the database state in order to know if we need to
     * create a basic admin user.
     */
    @Autowired
    private ResearcherService<Researcher, UUID> researcherService;

    /**
     * We use our {@code JournalService} to create new example journals.
     */
    @Autowired
    private JournalService<Journal, UUID> journalService;

    /**
     * In order to manipulate the acl_security_id table, we need to have an entity and an associated repository to work
     * with it programmatically. Note that this entity should not be used to initialize this table using JPA
     * initialization, instead, it has to be initialized manually by running the schema-postgresql.sql script, which is
     * done at the start of the application by Spring if it is configured to do so. The ACL infrastructure will not work
     * if the initialization has been done by JPA initializations.
     */
    @Autowired
    private SecurityIdRepository securityIdRepository;

    /**
     * This code run after the application is completely ready but just before it starts serving requests.
     * @param event Data associated with the event of the application being ready.
     */
    @Override
    public void onApplicationEvent(final @NotNull ApplicationReadyEvent event) {
        this.initializeDB();
    }

    /**
     * Initializes the database with basic data.
     */
    private void initializeDB()
    {
        Logger.getGlobal().info("INITIALIZING DB");
        this.initAdminResearcher();
        Logger.getGlobal().info("END INITIALIZING DB");

    }

    /**
     * Initializes an admin researcher with data.
     */
    private void initAdminResearcher() {
        // Init the admin user
        Researcher researcher = new Researcher();

        researcher.setSignalsAPIKey("basure");
        researcher.setAccountNonExpired(true);
        researcher.setEnabled(true);
        researcher.setCredentialsNonExpired(true);
        researcher.setPassword("chemistry");
        researcher.setUsername("eChempad@iciq.es");
        researcher.setAccountNonLocked(true);

        HashSet<Authority> authorities = new HashSet<>();
        authorities.add(new Authority("ROLE_ADMIN", researcher));
        authorities.add(new Authority("ROLE_USER", researcher));
        researcher.setAuthorities(authorities);

        // If the user is not in the DB create it
        if (this.researcherService.loadUserByUsername(researcher.getUsername()) == null)
        {
            // Authenticate user, or we will not be able to manipulate the ACL service with the security context empty
            Authentication auth = new UsernamePasswordAuthenticationToken(researcher.getUsername(), researcher.getPassword(), researcher.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Save of the authority is cascaded
            this.researcherService.save(researcher);

            // Insert role ROLE_ADMIN and ROLE_USER in the db, in acl_sid
            this.securityIdRepository.save(new SecurityId(false, "ROLE_ADMIN"));
            this.securityIdRepository.save(new SecurityId(false, "ROLE_USER"));

            // Now create some data associated with this admin user
            this.initJournal();
        }
    }

    /**
     * Initializes a test journal.
     */
    private void initJournal() {
        Journal journal = new Journal();

        journal.setDescription("The journal of the admin");
        journal.setName("Journal Admin");

        // Indirectly obtain the ID of the journal by saving it on the DB
        this.journalService.save(journal);
    }

    // Constructors

    public DatabaseInitStartup() {}
}
