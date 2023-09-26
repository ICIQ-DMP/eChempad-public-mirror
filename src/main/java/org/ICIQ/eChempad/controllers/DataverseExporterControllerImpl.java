/*
 * eChempad is a suite of web services oriented to manage the entire
 * data life-cycle of experiments and assays from Experimental
 * Chemistry and related Science disciplines.
 *
 * Copyright (C) 2021 - present Institut Català d'Investigació Química (ICIQ)
 *
 * eChempad is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.services.DataverseExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

/**
 * Exports data contents accessible by the logged user into a Dataverse repository instance.
 *
 * @see <a href="https://guides.dataverse.org/en/latest/api/intro.html">...</a>
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 24/10/2022
 */
@RestController
@RequestMapping("/api/dataverse")
public class DataverseExporterControllerImpl implements DataverseExporterController{

    @Autowired
    private DataverseExportService dataverseExportService;

    @GetMapping(
            value = "/exportJournal/{journal_id}",
            produces = "application/json"
    )
    @PreAuthorize("hasPermission(#journal_id, 'org.ICIQ.eChempad.entities.genericJPAEntities.Journal' , 'READ')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> exportJournal(@PathVariable UUID journal_id) {
        try
        {
            return ResponseEntity.ok(this.dataverseExportService.exportJournal(journal_id));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("Data from this Signals account could not have been exported.");
    }

    @GetMapping(
            value = "/exportJournalWithKey/{journal_id}",
            produces = "application/json"
    )
    @PreAuthorize("hasPermission(#journal_id, 'org.ICIQ.eChempad.entities.genericJPAEntities.Journal' , 'READ')")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<String> exportJournalWithKey(@PathVariable UUID journal_id, @RequestHeader(name = "x-api-key") String APIKey) {
        try
        {
            return ResponseEntity.ok(this.dataverseExportService.exportJournal(APIKey, journal_id));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("Data from this Signals account could not have been exported.");
    }

    // TODO
    @Override
    public ResponseEntity<String> exportWorkspace() {
        try
        {
            return ResponseEntity.ok(this.dataverseExportService.exportWorkspace());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("Data from this Signals account could not have been exported.");
    }

    // TODO
    @Override
    public ResponseEntity<String> exportWorkspace(String APIKey) {
        try
        {
            return ResponseEntity.ok(this.dataverseExportService.exportWorkspace(APIKey));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("Data from this Signals account could not have been exported.");
    }


}
