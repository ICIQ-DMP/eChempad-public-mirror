package org.ICIQ.eChempad.configurations;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

@Converter(autoApply = true)  // With autoapply = true performs automatic translation between types implicitly
public class PathConverter implements AttributeConverter<Path, String>, Serializable {

    @Override
    public String convertToDatabaseColumn(Path path) {
        return path == null ? null : path.toString();
    }

    @Override
    public Path convertToEntityAttribute(String s) {
        return s == null ? null : Paths.get(s);
    }
}