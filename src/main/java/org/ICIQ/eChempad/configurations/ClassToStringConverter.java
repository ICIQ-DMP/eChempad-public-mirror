package org.ICIQ.eChempad.configurations;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.Serializable;

/*
 * To transparently convert Class objects to Strings representing the name of the class from memory to DB and the
 * opposite.
 *
 * The Converter annotation has an element autoApply. If the value of this element is set to true (default is false),
 * the converter will be applied to all attributes of the target 'type', including to basic attribute values that are
 * contained within other, more complex attribute types. In this case we don't have to use @Convert annotation
 * explicitly on the attributes.
 */
@Converter(autoApply = true)  // With autoapply = true performs automatic translation between types implicitly
public class ClassToStringConverter implements AttributeConverter<Class<?>, String>, Serializable {

    @Override
    public String convertToDatabaseColumn(Class aClass) {
        return aClass.getName();
    }

    @Override
    public Class<?> convertToEntityAttribute(String s) {
        try
        {
            return Class.forName(s);
        }
        catch (ClassNotFoundException e)  // TODO: Assume fatal error on class not found
        {
            return null;
        }
    }
}