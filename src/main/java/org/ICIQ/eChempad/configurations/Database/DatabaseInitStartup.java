/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.Database;

import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.repositories.AclRepositoryImpl;
import org.ICIQ.eChempad.repositories.AuthorityRepository;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.ICIQ.eChempad.services.JournalService;
import org.ICIQ.eChempad.services.ResearcherService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.test.context.jdbc.Sql;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * This class contains the method
 * public void onApplicationEvent(final @NotNull ApplicationReadyEvent event)
 * which is executed after the application is "ready", which usually is after all the initializations, but before
 * accepting traffic. In here we can implement ways to load data to the database at the start when we are in dev
 * profile.
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
    private AclRepositoryImpl aclRepository;

    public DatabaseInitStartup() {}

    @Override
    public void onApplicationEvent(final @NotNull ApplicationReadyEvent event) {
        this.initializeDB();
    }

    private void initializeDB()
    {
        this.initAdminResearcher();
        // this.initJournal();
    }

    private void initAdminResearcher()
    {
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
        researcher.setPermissions(authorities);

        if (this.researcherRepository.findByUsername(researcher.getUsername()) == null)
        {
            Logger.getGlobal().info("_________________________________________________________________________________________________");
            this.researcherRepository.save(researcher);  // Save of the authority is cascaded
        }

    }

    private void initJournal()
    {
        Journal journal = new Journal();

        journal.setDescription("the example admin journal");
        journal.setName("H2O");

        String idOfJournalAlreadyInTheDatabase = "264a3c6e-21e6-450e-8cfc-cba1d97eb92d";

        Researcher res = (Researcher) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Logger.getGlobal().info(res.toString());
        Optional<Journal> journalDatabase = this.journalService.findById(UUID.fromString(idOfJournalAlreadyInTheDatabase));
        if (! journalDatabase.isPresent())
        {
            Journal journal2 = this.journalService.save(journal);
            this.aclRepository.addPermissionToUserInEntity(journal2, BasePermission.ADMINISTRATION, "eChempad@iciq.es");
            Logger.getGlobal().info(journal2.toString());
        }
    }

}
