/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;
import java.util.UUID;

/**
 * Used to limit the generic inheritance to entities to make them comply with this contract, which ensures the
 * possibility of manipulating its UUID.
 *
 * To use it, in a generic class that uses bounding parametrization such as Repository<T>, add inheritance to the
 * bounded parametrization in order to get access to the field UUID of an Entity, like:
 * class Repository<T extends IEntity> {
 *     ...
 * }
 * which will expose these methods for this specific entity.
 *
 * Of course entities will need to comply to this specification by also defining themselves as children of this
 * interface such as:
 * class Researcher implements IEntity {
 *     ...
 * }
 *
 * and implements the required methods by the interface.
 */
public interface IEntity {
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
     * Implemented by every class to return its own type, except for element permission, which returns the type of the
     * element that is giving permissions to.
     * @param <T> Parametrized type in order to return any type of class.
     * @return Class of the object implementing this interface.
     */
    //@JsonBackReference
    //<T extends IEntity> Class<T> getMyType();


    /**
     * Implemented differently by every entity class to know if the passed entity_uuid is contained by the current
     * object
     * @param entity_uuid UUID of an entity of type journal, experiment and document that is contained by this instance
     * @return True if the supplied UUID is contained by the current object.
     */
    //boolean isContainer(UUID entity_uuid);


    /**
     * Implemented differently by every entity class to know if the passed entity_uuid is parent of the current
     * instance.
     * @param entity_uuid UUID of the entity of type journal, experiment and document that can contain this instance
     * @return True if the supplied UUID is a container of the current object.
     */
    //boolean isContained(UUID entity_uuid);
}
