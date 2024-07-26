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


import org.springframework.stereotype.Component;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.Serializable;
import java.util.UUID;

/**
 * Provides automatic transparent conversion between UUID and its String representation. This is used by Spring Boot
 * automatically when retrieving and saving from and to the database. The {@code @autoApply = true} makes the methods
 * of this class apply when necessary This code is not used directly by our business logic.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 04/10/2022
 */
@Component
@Converter(autoApply = false)
public class UUIDConverter implements AttributeConverter<UUID, String>, Serializable {

    /**
     * Provides the {@code String} representation of a {@code UUID} object by calling its {@code toString} method.
     *
     * @param uuid {@code UUID} instance.
     * @return The {@code String} representation of the supplied {@code UUID}.
     */
    @Override
    public String convertToDatabaseColumn(UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }

    /**
     * Provides the {@code UUID} representation of a String object by calling its {@code toString} method.
     *
     * @param s {@code String} instance.
     * @return The {@code UUID} representation of the supplied {@code String}.
     */
    @Override
    public UUID convertToEntityAttribute(String s) {
        return s == null ? null : UUID.fromString(s);
    }
}
