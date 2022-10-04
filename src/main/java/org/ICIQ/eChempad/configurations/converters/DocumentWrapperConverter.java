package org.ICIQ.eChempad.configurations.converters;

import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.DocumentWrapper;
import org.ICIQ.eChempad.services.LobService;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;


import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.logging.Logger;

// Configuration annotation because we use explicitly this converter, so we need it to be a bean in order to autowire
@Configuration
@Repository
@Transactional
@Converter
public class DocumentWrapperConverter implements AttributeConverter<DocumentWrapper, Document>, Serializable {

    @Autowired
    private LobService lobService;

    @Override
    @Transactional
    public DocumentWrapper convertToEntityAttribute(Document document) {

        DocumentWrapper documentWrapper = new DocumentWrapper();

        String name = document.getName();
        documentWrapper.setName(name);
        documentWrapper.setDescription(document.getDescription());
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

    @Override
    @Transactional
    public Document convertToDatabaseColumn(DocumentWrapper documentWrapper) {
        Document document = new Document();

        document.setName(documentWrapper.getName());
        document.setDescription(documentWrapper.getDescription());
        document.setId(documentWrapper.getId());
        document.setContentType(documentWrapper.getContentType());
        document.setFileSize(documentWrapper.getSize());
        document.setOriginalFilename(documentWrapper.getOriginalFilename());


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