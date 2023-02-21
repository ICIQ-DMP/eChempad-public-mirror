package org.ICIQ.eChempad.configurations.utilities;

import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.ICIQ.eChempad.services.genericJPAServices.JournalService;

import javax.inject.Inject;
import java.util.UUID;

public class OverviewVM extends AbstractVM {

    @Inject
    private JournalService<Journal, UUID> journalService;
}
