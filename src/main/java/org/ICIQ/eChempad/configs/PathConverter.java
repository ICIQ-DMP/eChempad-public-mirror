package org.ICIQ.eChempad.configs;

import java.nio.file.Path;
import java.nio.file.Paths;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/*
 * https://stackoverflow.com/questions/53199558/hibernate-mapping-exception-could-not-determine-type-for-java-nio-file-path
 * To transparently convert Path objects to String from DB to memory and the opposite.
 *
 * "The Converter annotation has an element autoApply. If the value of this element is set to true (default is false),
 * the converter will be applied to all attributes of the target 'type', including to basic attribute values that are
 * contained within other, more complex attribute types. In this case we don't have to use @Convert annotation
 * explicitly on the attributes."
 */
@Converter(autoApply = true)  // With autoapply = true performs automatic translation between types transparently
public class PathConverter implements AttributeConverter<Path, String> {

    @Override
    public String convertToDatabaseColumn(Path path) {
        return path == null ? null : path.toString();
    }

    @Override
    public Path convertToEntityAttribute(String s) {
        return s == null ? null : Paths.get(s);
    }
}
