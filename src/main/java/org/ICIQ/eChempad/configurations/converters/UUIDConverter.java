/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.converters;


import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
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
@Converter(autoApply = true)
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
