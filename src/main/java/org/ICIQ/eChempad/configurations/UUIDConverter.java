/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations;


import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.Serializable;
import java.util.UUID;

/*
 * https://stackoverflow.com/questions/53199558/hibernate-mapping-exception-could-not-determine-type-for-java-nio-file-path
 * To transparently convert UUID objects to String from DB to memory and the opposite.
 *
 * "The Converter annotation has an element autoApply. If the value of this element is set to true (default is false),
 * the converter will be applied to all attributes of the target 'type', including to basic attribute values that are
 * contained within other, more complex attribute types. In this case we don't have to use @Convert annotation
 * explicitly on the attributes."
 */
@Converter(autoApply = true)  // With autoapply = true performs automatic translation between types implicitly
public class UUIDConverter implements AttributeConverter<UUID, String>, Serializable {

    @Override
    public String convertToDatabaseColumn(UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }

    @Override
    public UUID convertToEntityAttribute(String s) {
        return s == null ? null : UUID.fromString(s);
    }
}
