/*
 * eChempad is a suite of web services oriented to manage the entire
 * data life-cycle of experiments and assays from Experimental
 * Chemistry and related Science disciplines.
 *
 * Copyright (C) 2021 - present Institut Català d'Investigació Química (ICIQ)
 *
 * eChempad is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.ICIQ.eChempad.configurations.converters;

import org.springframework.http.MediaType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

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
