package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.services.JournalService;
import org.ICIQ.eChempad.services.ResearcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.UUID;

@RestController
@RequestMapping("/api/journal")
public class JournalControllerImpl<T extends IEntity, S extends Serializable> extends GenericControllerImpl<Journal, UUID> implements JournalController<Journal, UUID> {

    @Autowired
    public JournalControllerImpl(JournalService<Journal, UUID> journalService) {
        super(journalService);
    }
}
