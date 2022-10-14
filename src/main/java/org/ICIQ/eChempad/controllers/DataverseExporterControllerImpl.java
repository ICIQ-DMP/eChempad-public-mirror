package org.ICIQ.eChempad.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/dataverse")
public class DataverseExporterControllerImpl implements DataverseExporterController{


    @Override
    public void exportJournal(UUID journal_uuid) {

    }

    @Override
    public void exportWorkspace() {

    }
}
