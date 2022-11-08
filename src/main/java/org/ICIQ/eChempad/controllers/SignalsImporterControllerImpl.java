package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.services.SignalsImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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
    public ResponseEntity<String> importWorkspace(String APIKey) {
        try {
            return ResponseEntity.ok().body(this.signalsImportService.importWorkspace(APIKey));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("Data from this Signals account could not have been imported.");
    }

    @Override
    // TODO
    public ResponseEntity<String> importJournal() {
        return null;
    }

    @Override
    // TODO
    public ResponseEntity<String> importJournal(String APIKey) {
        return null;
    }


}
