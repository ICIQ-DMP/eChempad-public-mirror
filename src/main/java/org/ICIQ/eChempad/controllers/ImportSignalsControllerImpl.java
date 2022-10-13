package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.services.ImportSignalsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/import/signals")
public class ImportSignalsControllerImpl implements ImportSignalsController{

    @Autowired
    private ImportSignalsService importSignalsService;

    @Override
    @GetMapping(value = "/bulk")
    public ResponseEntity<String> bulkImportSignals() {
        try {
            return ResponseEntity.ok().body(this.importSignalsService.bulkImport());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("Data from this Signals account could not have been imported.");
    }

    @Override
    @GetMapping(value = "/bulkWithKey")
    public ResponseEntity<String> bulkImportSignals(String APIKey) {
        try {
            return ResponseEntity.ok().body(this.importSignalsService.bulkImport(APIKey));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("Data from this Signals account could not have been imported.");    }
}
