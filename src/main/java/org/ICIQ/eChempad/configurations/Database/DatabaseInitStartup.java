/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.Database;

import org.ICIQ.eChempad.configurations.Utilities.PermissionBuilder;
import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.configurations.Security.AclServiceCustomImpl;
import org.ICIQ.eChempad.entities.SecurityId;
import org.ICIQ.eChempad.repositories.SecurityIdRepository;
import org.ICIQ.eChempad.repositories.AuthorityRepository;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.ICIQ.eChempad.services.JournalService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.logging.Logger;

/**
 * This class contains the method
 * public void onApplicationEvent(final @NotNull ApplicationReadyEvent event)
 * which is executed after the application is "ready", which usually is after all the initializations, but before
 * accepting traffic. In here we can implement ways to load data to the database at the start.
 */
@Component
public class DatabaseInitStartup implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private ResearcherRepository<Researcher, UUID> researcherRepository;

    @Autowired
    private JournalService<Journal, UUID> journalService;

    @Autowired
    private AuthorityRepository<Authority, UUID> authorityRepository;

    @Autowired
    private AclServiceCustomImpl aclService;

    @Autowired
    private SecurityIdRepository securityIdRepository;

    public DatabaseInitStartup() {}

    @Override
    public void onApplicationEvent(final @NotNull ApplicationReadyEvent event) {
        this.initializeDB();
    }

    private void initializeDB()
    {
        // this.initJournal();
        // Authentication required; nested exception is java.lang.IllegalArgumentException: Authentication required



        this.initAdminResearcher();
    }

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
        researcher.setAuthorities(authorities);

        // If the user is not in the DB create it
        if (this.researcherRepository.findByUsername(researcher.getUsername()) == null)
        {
            researcher = this.researcherRepository.save(researcher);  // Save of the authority is cascaded

            // Authenticate user, or we will not be able to manipulate the ACL service with the security context empty
            Authentication auth = new UsernamePasswordAuthenticationToken(researcher.getUsername(), null, researcher.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Insert role ROLE_AADMIN and ROLE_USER in the db, in acl_sid
            SecurityId securityIdAdmin = this.securityIdRepository.save(new SecurityId(false, "ROLE_ADMIN"));
            SecurityId securityIdUser = this.securityIdRepository.save(new SecurityId(false, "ROLE_USER"));

            // Add ACL permissions, so the admin user can edit its own details
            Iterator<Permission> it = PermissionBuilder.getFullPermissionsIterator();
            while (it.hasNext())
            {
                this.aclService.addPermissionToUserInEntity(researcher, it.next(), researcher.getUsername());
            }


            Logger.getGlobal().warning("init end");
            // If the admin user does not exist, create some data for it

        }
    }

    private void initJournal() {
        Journal journal = new Journal();

        journal.setDescription("the example admin journal");
        journal.setName("H2O");

        String idOfJournalAlreadyInTheDatabase = "264a3c6e-21e6-450e-8cfc-cba1d97eb92d";

        Optional<Journal> journalDatabase = this.journalService.findById(UUID.fromString(idOfJournalAlreadyInTheDatabase));

        journal.setId(UUID.randomUUID());
        this.aclService.addPermissionToUserInEntity(journal, BasePermission.ADMINISTRATION, "eChempad@iciq.es");
        if (! journalDatabase.isPresent())
        {
            Journal journal2 = this.journalService.save(journal);
        }
    }

}
