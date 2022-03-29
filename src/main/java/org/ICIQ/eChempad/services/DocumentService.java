package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.configurations.UploadFileResponse;
import org.ICIQ.eChempad.entities.Document;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface DocumentService extends GenericService<Document, UUID> {

    void addDocumentToExperiment(Document document, MultipartFile file, UUID experiment_uuid);

}
