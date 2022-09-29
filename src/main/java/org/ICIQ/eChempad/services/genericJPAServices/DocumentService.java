package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.configurations.wrappers.DocumentWrapper;
import org.ICIQ.eChempad.entities.genericJPAEntities.Authority;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntityImpl;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.springframework.core.io.Resource;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public interface DocumentService<T extends JPAEntityImpl, S extends Serializable> extends GenericService<Document, UUID> {

    /**
     * Adds a document to a certain experiment using the data in the document helper class and returns the new Document
     * instance.
     * @param document Data of a detached document instance inside a helper class that should have all the equivalent
     *                 fields.
     * @param experiment_uuid UUID of the experiment that we want to edit by adding this document.
     * @return Managed instance of the created document.
     */
    Document addDocumentToExperiment(DocumentWrapper document, UUID experiment_uuid);

    /**
     * Get all documents that belong to a certain experiment designated by its UUID.
     * @param experiment_uuid UUID of the experiment we are retrieving.
     * @return Set of documents of this experiment. It could be empty.
     */
    Set<Document> getDocumentsFromExperiment(UUID experiment_uuid);

    /**
     * Obtains the file stream associated with a document if there is enough permissions to read it.
     * @param document_uuid Used to uniquely identify the document in the file DB.
     * @return ByteArray response (binary response).
     * @throws ResourceNotExistsException Thrown if the UUID does not exist for any document.
     * @throws NotEnoughAuthorityException Thrown if we do not have read permissions against the document.
     */
    Resource getDocumentData(UUID document_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

}
