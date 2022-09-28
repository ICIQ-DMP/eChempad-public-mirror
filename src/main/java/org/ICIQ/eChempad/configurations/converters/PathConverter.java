package org.ICIQ.eChempad.configurations.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides automatic transparent conversion between a Path object and its String representation.
 */
@Converter(autoApply = true)
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