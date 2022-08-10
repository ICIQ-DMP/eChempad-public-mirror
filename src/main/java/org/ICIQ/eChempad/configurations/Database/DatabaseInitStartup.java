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
import org.ICIQ.eChempad.repositories.AuthorityRepository;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.ICIQ.eChempad.services.JournalService;
import org.ICIQ.eChempad.services.ResearcherService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
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
    private ResearcherService<Researcher, UUID> researcherService;

    @Autowired
    private JournalService<Journal, UUID> journalService;

    @Autowired
    private AuthorityRepository<Authority, UUID> authorityRepository;

    public DatabaseInitStartup() {}

    @Override
    public void onApplicationEvent(final @NotNull ApplicationReadyEvent event) {
        this.initializeDB();
    }

    private void initializeDB()
    {
        this.initAdminResearcher();
        this.initJournal();
    }

    private void initAdminResearcher()
    {
        Researcher researcher = new Researcher();
        researcher.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        researcher.setSignalsAPIKey("basure");
        researcher.setAccountNonExpired(true);
        researcher.setEnabled(true);
        researcher.setCredentialsNonExpired(true);
        researcher.setPassword("chemistry");
        researcher.setUsername("eChempad@iciq.es");
        researcher.setAccountNonLocked(true);

        Authority authority = null;
        if (this.researcherService.getById((UUID) researcher.getId()) == null)
        {
            authority = this.authorityRepository.save(new Authority("ROLE_ADMIN", researcher));
            researcher = this.researcherService.save(researcher);
        }
    }

    private void initJournal()
    {
        Journal journal = new Journal();
        journal.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        journal.setDescription("the example journal");
        journal.setName("H2O");

        if (this.journalService.getById((UUID) journal.getId()) == null)
        {
            this.journalService.save(journal);
        }
    }

}
