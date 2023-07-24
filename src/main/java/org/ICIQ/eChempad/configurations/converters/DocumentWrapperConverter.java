package org.ICIQ.eChempad.configurations.converters;

import org.ICIQ.eChempad.entities.DocumentWrapper;
import org.ICIQ.eChempad.services.LobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;


import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;


/**
 * This class provides two methods to bidirectionally transform instances of {@code Document} and
 * {@code DocumentWrapper} entity types.
 * <p>
 * {@code Document} is the entity type used to store documents in the database, while {@code DocumentWrapper} is the
 * entity used to transmit documents over the Internet.
 * <p>
 * This class implements the {@code AttributeConverter} interface for these two types. This models the functionality
 * of our class and gives us the adequate contract to fulfill.
 * <p>
 * Also, the class is annotated with {@code @Converter} to make the warning about an {@code AttributeConverter}
 * not being annotated with {@code Converter} disappear.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 1.0
 */
@Component
@Converter
public class DocumentWrapperConverter implements AttributeConverter<DocumentWrapper, org.ICIQ.eChempad.entities.genericJPAEntities.Document>, Serializable {

    /**
     * Service used to manipulate LOBs from the database. It allows creation and reading of LOBs.
     */
    @Autowired
    private LobService lobService;

    public DocumentWrapperConverter() {}

    /**
     * Creates a {@code DocumentWrapper} detached instance from the data of the supplied {@code Document}.
     * <p>
     * If in this method we get a {@code java.sql.SQLException: could not reset reader} it is caused by the implementation
     * of LOB types by Hibernate. In our case the implementation only allows to read the {@code InputStream} from
     * the LOB once. If the read is tried again this {@code Exception} will show up. To solve it, provide a "fresh" instance
     * of {@code Document} before calling any of the two methods in this class. You can do it by calling the
     * {@code DocumentRepository} to retrieve the instance that you wanted to have, in order to "refresh" the BLOB.
     *
     * @param document Entity presumably coming from the database, which contains the data to bulk into the return
     * {@code DocumentWrapper}.
     * @return {@code DocumentWrapper} containing all data of the supplied {@code Document}
     */
    @Transactional
    @Override
    public DocumentWrapper convertToEntityAttribute(org.ICIQ.eChempad.entities.genericJPAEntities.Document document) {

        DocumentWrapper documentWrapper = new DocumentWrapper();

        documentWrapper.setId(document.getId());
        documentWrapper.setName(document.getName());
        documentWrapper.setDescription(document.getDescription());
        documentWrapper.setCreationDate(document.getCreationDate());
        documentWrapper.setId(document.getId());

        MultipartFile multipartFile = null;
        try {
            InputStream is = this.lobService.readBlob(document.getBlob());

            multipartFile = new MockMultipartFile(document.getName(), document.getOriginalFilename(), document.getContentType().toString(), is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        documentWrapper.setFile(multipartFile);

        return documentWrapper;
    }

    /**
     * Creates a {@code Document} detached instance from the data of the supplied {@code DocumentWrapper}.
     *
     * @param documentWrapper Entity presumably coming from outside our application, which contains the data to bulk
     * into the return {@code Document}.
     * @return {@code Document} containing all data of the supplied {@code DocumentWrapper}
     */
    @Transactional
    @Override
    public org.ICIQ.eChempad.entities.genericJPAEntities.Document convertToDatabaseColumn(DocumentWrapper documentWrapper) {
        org.ICIQ.eChempad.entities.genericJPAEntities.Document document = new org.ICIQ.eChempad.entities.genericJPAEntities.Document();

        document.setId(documentWrapper.getId());
        document.setName(documentWrapper.getName());
        document.setDescription(documentWrapper.getDescription());
        document.setCreationDate(documentWrapper.getCreationDate());
        document.setId(documentWrapper.getId());
        document.setContentType(documentWrapper.getContentType());
        document.setFileSize(documentWrapper.getSize());
        document.setOriginalFilename(documentWrapper.getOriginalFilename());

        // Create BLOB from InputStream
        Blob blob = null;
        try {
            blob = this.lobService.createBlob(documentWrapper.getFile().getInputStream(), documentWrapper.getFile().getSize());
        } catch (IOException e) {
            e.printStackTrace();
        }

        document.setBlob(blob);

        return document;
    }
}