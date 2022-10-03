package org.ICIQ.eChempad.configurations.converters;

import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.DocumentWrapper;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.mock.web.MockMultipartFile;


import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;

// Configuration annotation because we use explicitly this converter, so we need it to be a bean in order to autowire
@Configuration
@Converter(autoApply = true)
public class DocumentWrapperConverter implements AttributeConverter<DocumentWrapper, Document>, Serializable {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public DocumentWrapper convertToEntityAttribute(Document document) {

        DocumentWrapper documentWrapper = new DocumentWrapper();

        documentWrapper.setName(document.getName());
        documentWrapper.setDescription(document.getDescription());
        documentWrapper.setId(document.getId());


        MultipartFile multipartFile = null;
        try {
            multipartFile = new MockMultipartFile(document.getName(), document.getOriginalFilename(), document.getContentType().toString(), document.getBlob().getBinaryStream());
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        documentWrapper.setFile(multipartFile);

        return documentWrapper;
    }

    @Override
    public Document convertToDatabaseColumn(DocumentWrapper documentWrapper) {
        Document document = new Document();

        document.setName(documentWrapper.getName());
        document.setDescription(documentWrapper.getDescription());
        document.setId(documentWrapper.getId());

        Blob blob = null;
        try {
            blob = this.sessionFactory.getCurrentSession().getLobHelper().createBlob(documentWrapper.getFile().getInputStream(), documentWrapper.getFile().getSize());
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.setBlob(blob);

        return document;
    }
}