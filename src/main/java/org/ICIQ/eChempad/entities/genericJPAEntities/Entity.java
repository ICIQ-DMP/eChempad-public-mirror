package org.ICIQ.eChempad.entities.genericJPAEntities;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Used to limit the generic inheritance to entities to make them comply with this contract, which ensures the
 * possibility of manipulating its UUID for all the entities in our project.
 * <p>
 * To use it, in a generic class that uses bounding parametrization such as Repository<T>, add inheritance to the
 * bounded parametrization in order to get access to the field UUID of an Entity, like:
 * class Repository<T extends IEntity> {
 *     ...
 * }
 * which will expose these methods for this specific entity.
 * <p>
 * Of course entities will need to comply to this specification by also defining themselves as children of this
 * interface such as:
 * class Researcher implements IEntity {
 *     ...
 * }
 * <p>
 * and implements the required methods by the interface.
 * <p>
 * This interface defines the contract that need to fulfill all model classes for the entities in the database.
 */


public interface Entity {
    /**
     * Exposes and returns the UUID of an entity.
     * @return UUID of the entity.
     */
    Serializable getId();

    /**
     * Sets the UUID of an entity.
     * This is a method that will have collisions with hibernate because hibernate uses the id field as a PK
     * (Primary Key) for accessing the database. As such, this method has to be only used against entities that are
     * not managed by hibernate.
     * This interface is specially designed to expose this specific method of all the entities, and is specially
     * designed to perform updates of existing entities of the database when an ID is not supplied with the received
     * data object.
     * @param id ID that will be set. Only usable on dettached spring boot instances
     */
    void setId(Serializable id);

    /**
     * Implemented by every class to return its own type.
     * @param <T> Parametrized type in order to return any type of class.
     * @return Class of the object implementing this interface.
     */
    @JsonIgnore
    <T extends Entity> Class<T> getType();

    /**
     * Obtains the typeName, used by jackson to deserialize generics.
     * @return Name of the class as string.
     */
    @JsonIgnore
    default String getTypeName()
    {
        return this.getType().getCanonicalName();
    }

    /**
     * Gets the creation of an entity.
     *
     * @return Date object with the creation date of the entity.
     */
    @JsonIgnore
    Date getCreationDate();

    /**
     * Sets the creation date of an entity.
     *
     * @param creationDate Date object with the new creation date of the entity.
     */
    @JsonIgnore
    void setCreationDate(Date creationDate);

    /**
     * Sets the creation date of the element. This method is needed since the fields need to be explicitly initialized
     * outside the constructors of the entity.
     */
    @JsonIgnore
    void initCreationDate();
}
