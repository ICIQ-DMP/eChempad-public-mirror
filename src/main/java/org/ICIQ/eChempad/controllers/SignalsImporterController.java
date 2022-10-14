package org.ICIQ.eChempad.controllers;

import org.springframework.http.ResponseEntity;


public interface SignalsImporterController {

    ResponseEntity<String> bulkImportSignals();

    ResponseEntity<String> bulkImportSignals(String APIKey);


}
