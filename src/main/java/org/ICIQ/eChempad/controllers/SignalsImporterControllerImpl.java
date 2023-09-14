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

import org.ICIQ.eChempad.services.importServices.SignalsImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.Serializable;

/**
 * Exposes method calls in the CRUD API in order to import contents from third-party services. As a class it basically
 * forwards the call to the {@code SignalsImportService} and performs small data manipulation related with request and
 * connection.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 14/10/2022
 */
@RestController
@RequestMapping("/api/import/signals")
public class SignalsImporterControllerImpl implements SignalsImporterController {

    @Autowired
    private SignalsImportService signalsImportService;

    @Override
    @GetMapping(value = "/import")
    public ResponseEntity<String> importWorkspace() {
        try {
            return ResponseEntity.ok().body(this.signalsImportService.importWorkspace());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("Data from this Signals account could not have been imported.");
    }

    @Override
    @GetMapping(value = "/importWithKey")
    public ResponseEntity<String> importWorkspace(@RequestHeader("x-api-key") String APIKey) {
        try {
            return ResponseEntity.ok().body(this.signalsImportService.importWorkspace(APIKey));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("Data from this Signals account could not have been imported.");
    }

    @Override
    @GetMapping(value = "/importJournal/{id}")
    public ResponseEntity<String> importJournal(@PathVariable(value = "id") Serializable eid) {
        return ResponseEntity.ok(this.signalsImportService.importJournal(eid));
    }

    @Override
    @GetMapping(value = "/importJournalWithKey/{id}")
    public ResponseEntity<String> importJournal(@RequestHeader("x-api-key") String APIKey, @PathVariable(value = "id") Serializable eid) {
        return ResponseEntity.ok(this.signalsImportService.importJournal(APIKey, eid));
    }
}
