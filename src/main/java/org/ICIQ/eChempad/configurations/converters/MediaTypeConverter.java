package org.ICIQ.eChempad.configurations.converters;

import org.springframework.http.MediaType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.logging.Logger;

/**
 * Provides automatic transparent conversion between a {@code MediaType} and its {@code String} representation. This is
 * used by Spring Boot automatically when retrieving and saving from and to the database. The {@code @autoApply = true}
 * makes the methods of this class apply when necessary. This code is not used directly by our business logic. This
 * class is used to serialize and deserialize the {@code MediaType} fields present in the {@code Document} class.
 * {@code MediaType} can be understood as a MimeType.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 14/10/2022
 */
@Converter(autoApply = true)
public class MediaTypeConverter implements AttributeConverter<MediaType, String>{

    /**
     * Converts a {@code MediaType} instance into its {@code String representation}.
     *
     * @param attribute Unmarshalled data.
     * @return Marshalled {@code MediaType} into an {@code String}.
     */
    @Override
    public String convertToDatabaseColumn(MediaType attribute) {
        return attribute.toString();
    }

    /**
     * Converts a {@code String} instance into its {@code MediaType} representation.
     *
     * @param dbData Marshalled data.
     * @return Unmarshalled {@code String} into its {@code MediaType} representation
     */
    @Override
    public MediaType convertToEntityAttribute(String dbData) {
        return MediaType.parseMediaType(dbData);
    }
}
