package org.ICIQ.eChempad.configurations.converters;

import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.DocumentWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

// Configuration annotation because we use explicitly this converter, so we need it to be a bean in order to autowire
@Configuration
@Converter(autoApply = true)
public class DocumentWrapperConverter implements AttributeConverter<DocumentWrapper, Document>, Serializable {

    @Override
    public DocumentWrapper convertToEntityAttribute(Document document) {

        DocumentWrapper documentWrapper = new DocumentWrapper();

        documentWrapper.setName(document.getName());
        documentWrapper.setDescription(document.getDescription());
        documentWrapper.setId(document.getId());
        //documentWrapper.setFile();

        return documentWrapper;
    }

    @Override
    public Document convertToDatabaseColumn(DocumentWrapper documentWrapper) {
        Document document = new Document();

        document.setName(documentWrapper.getName());
        document.setDescription(documentWrapper.getDescription());
        document.setId(documentWrapper.getId());

        return document;
    }
}