package org.ICIQ.eChempad.configurations.Converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A converter is a class that defines two methods to interchange the format between two classes. It is particularly
 * used when in need to convert a memory object to a serializable type when introducing it into the DB and vice-versa.
 */
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