package org.ICIQ.eChempad.configurations.converters;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ICIQ.eChempad.entities.DocumentWrapper;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.Serializable;

// TODO
@Converter
public class JournalToObjectNodeConverter implements AttributeConverter<Journal, ObjectNode> {


    @Override
    public ObjectNode convertToDatabaseColumn(Journal attribute) {
        return null;
    }

    @Override
    public Journal convertToEntityAttribute(ObjectNode dbData) {
        return null;
    }
}
