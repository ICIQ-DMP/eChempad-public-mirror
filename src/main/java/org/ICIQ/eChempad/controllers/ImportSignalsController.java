package org.ICIQ.eChempad.controllers;

import org.springframework.http.ResponseEntity;

public interface ImportSignalsController {

    ResponseEntity<String> bulkImportSignals();

    ResponseEntity<String> bulkImportSignals(String APIKey);


}
