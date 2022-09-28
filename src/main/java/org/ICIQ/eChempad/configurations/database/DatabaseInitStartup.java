/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.database;

import org.ICIQ.eChempad.configurations.utilities.PermissionBuilder;
import org.ICIQ.eChempad.entities.genericJPAEntities.Authority;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.ICIQ.eChempad.entities.genericJPAEntities.Researcher;
import org.ICIQ.eChempad.configurations.security.ACL.AclServiceCustomImpl;
import org.ICIQ.eChempad.entities.SecurityId;
import org.ICIQ.eChempad.repositories.SecurityIdRepository;
import org.ICIQ.eChempad.repositories.genericJPARepositories.AuthorityRepository;
import org.ICIQ.eChempad.repositories.genericJPARepositories.ResearcherRepository;
import org.ICIQ.eChempad.services.genericJPAServices.JournalService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;

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
        authorities.add(new Authority("ROLE_USER", researcher));
        researcher.setAuthorities(authorities);

        // If the user is not in the DB create it
        if (this.researcherRepository.findByUsername(researcher.getUsername()) == null)
        {
            // Save of the authority is cascaded
            researcher = this.researcherRepository.save(researcher);

            // Authenticate user, or we will not be able to manipulate the ACL service with the security context empty
            Authentication auth = new UsernamePasswordAuthenticationToken(researcher.getUsername(), null, researcher.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Insert role ROLE_AADMIN and ROLE_USER in the db, in acl_sid
            SecurityId securityIdAdmin = this.securityIdRepository.save(new SecurityId(false, "ROLE_ADMIN"));
            SecurityId securityIdUser = this.securityIdRepository.save(new SecurityId(false, "ROLE_USER"));

            // Add ACL permissions, so the admin user can edit its own researcher entity
            Iterator<Permission> it = PermissionBuilder.getFullPermissionsIterator();
            while (it.hasNext())
            {
                this.aclService.addPermissionToUserInEntity(researcher, it.next(), researcher.getUsername());
            }

            // Now create some data associated with this admin user
            this.initJournal();
        }
    }

    private void initJournal() {
        Journal journal = new Journal();

        journal.setDescription("The journal of the admin");
        journal.setName("Journal Admin");

        // Indirectly obtain the ID of the journal by saving it on the DB
        Journal journal2 = this.journalService.save(journal);

        Iterator<Permission> it = PermissionBuilder.getFullPermissionsIterator();
        while (it.hasNext())
        {
            this.aclService.addPermissionToUserInEntity(journal2, it.next(), "eChempad@iciq.es");
        }
    }

}
