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
package org.ICIQ.eChempad.entities.genericJPAEntities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.ICIQ.eChempad.configurations.converters.UUIDConverter;
import org.ICIQ.eChempad.entities.DocumentWrapper;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * This class is the generic type that can be seen as {@code T} in the repositories, services and controllers of this
 * application. This abstract class is used in order to be able to use certain annotations that would not work directly
 * in an interface, in our case {@code JPAEntity}.
 * <p>
 * A relevant annotation regarding this matter is the annotation {@code @JsonInclude(JsonInclude.Include.NON_NULL)},
 * which makes Jackson ignore null fields when deserializing into a memory entity. This means that if some fields that
 * are expected in JSON for a certain entity implementing this class are not present in the JSON, Jackson will ignore
 * the values and not set them into the parsed entity. The default behaviour for a "data" field is set them to null if
 * they are not present in the JSON.
 * <p>
 * There is also {@code JsonTypeInfo}, which is used to create a field called "typeName" that contains the actual type
 * of the instance that should be parsed from the JSON and {@code JsonSubTypes}, which is used to declare which values
 * are allowed into this "typeName" field and what is the correspondence to entity classes.
  */

@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
@JsonTypeInfo(
        use=JsonTypeInfo.Id.NAME,
        include=JsonTypeInfo.As.EXISTING_PROPERTY,
        property="typeName")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Container.class, name = "Container"),
        @JsonSubTypes.Type(value = Authority.class, name = "Authority"),
        @JsonSubTypes.Type(value = Researcher.class, name = "Researcher"),
        @JsonSubTypes.Type(value = DocumentWrapper.class, name = "Document")
})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class EntityImpl implements Entity {

    @Id
    @Convert(converter = UUIDConverter.class)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", nullable = false, unique = true)
    protected UUID id;

    /**
     * Date of creation of the entity.
     */
    @Column(nullable = false)
    private Date creationDate;

    public EntityImpl() {
        this.initCreationDate();
    }

    /**
     * Implemented by every class to return its own type.
     *
     * @return Class of the object implementing this interface.
     */
    @Override
    public abstract <T extends Entity> Class<T> getType();

    /**
     * Obtains the typeName, used by jackson to deserialize generics.
     *
     * @return Name of the class as string.
     */
    @Override
    public String getTypeName() {
        return this.getType().getName();
    }

    @Override
    public void initCreationDate() {
        this.creationDate = new Date();
    }

    // GETTERS AND SETTERS


    @Override
    public Serializable getId() {
        return this.id;
    }

    /**
     * Sets the UUID of an entity.
     * This is a method that will have collisions with hibernate because hibernate uses the id field as a PK
     * (Primary Key) for accessing the database. As such, this method has to be only used against entities that are
     * not managed by hibernate.
     * This interface is specially designed to expose this specific method of all the entities, and is specially
     * designed to perform updates of existing entities of the database when an ID is not supplied with the received
     * data object.
     *
     * @param id ID that will be set. Only usable on detached spring boot instances.
     */
    @Override
    public void setId(Serializable id) {
        this.id = (UUID) id;
    }

    @Override
    public Date getCreationDate() {
        return this.creationDate;
    }

    @Override
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
