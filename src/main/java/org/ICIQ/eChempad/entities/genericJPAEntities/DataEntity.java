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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * This interface extends the contract that a model class needs to fulfill in order to properly model an entity of the
 * database that is also used to store information.
 * <p>
 * This interface provides common methods that data entities in the database have. These methods usually suggest the
 * existence of certain data fields in the implementing classes.
 */
public interface DataEntity extends Entity{

    /**
     * Returns a name for this data entity.
     * @return name of the current data entity.
     */
    String getName();

    /**
     * Sets the name of this data entity.
     * @param name New name of the current data entity.
     */
    void setName(String name);

    /**
     * Returns the description for this data entity.
     * @return Description of the current data entity.
     */
    String getDescription();

    /**
     * Sets the name of this data entity.
     * @param description New description of the current data entity.
     */
    void setDescription(String description);

    /**
     * Gets the parent entity of the current entity.
     *
     * @return the parent entity.
     */
    Container getParent();

    /**
     * Sets the parent entity of the current entity.
     *
     * @param parent the new parent entity.
     */
    void setParent(Container parent);

    /**
     * Gets the creation of an entity.
     *
     * @return Date object with the creation date of the entity.
     */
    Date getCreationDate();

    /**
     * Sets the creation date of the element. This method is needed since the fields need to be explicitly initialized
     * outside the constructors of the entity. This is a special method to initialize the creation date, which has to be
     * the current time when executing this code.
     */
    void initCreationDate();

    /**
     * Sets the creation date of this DataEntity. Used to manage internally this type.
     *
     * @param creationDate A creation date as a Date object.
     */
    void setCreationDate(Date creationDate);

    /**
     * Gets the last time on which this DataEntity was edited.
     *
     * @return The last edition date as a {@code Date} object.
     */
    Date getLastEditionDate();

    /**
     * Updates the last time this {@code DataEntity} was edited with the current time.
     */
    void updateLastEditionDate();

    /**
     * Sets the last edition date. Used to manage internally this field.
     *
     * @param lastEditionDate Last edition data as a Date object.
     */
    void setLastEditionDate(Date lastEditionDate);

    /**
     * Gets the last time on which the data that this DataEntity corresponds to was edited in the origin platform.
     *
     * @return The last edition date in the origin platform as a {@code Date} object.
     */
    Date getOriginLastEditionDate();

    /**
     * Sets the last time this {@code DataEntity} was edited with the supplied parameter.
     *
     * @param originLastEditionDate The last date that this element was modified in the origin platform.
     */
    void setOriginLastEditionDate(Date originLastEditionDate);

    /**
     * Gets the id that identifies this resource in the original platform that it came from. If it is created in
     * eChempad, will be null.
     *
     * @return It may be a String that has different codifications.
     */
    String getOriginId();

    /**
     * Sets the originId field, which is used to identify this resource in the original platform that ir came from.
     *
     * @param originId A String that will replace the data in the originId field.
     */
    void setOriginId(String originId);

    /**
     * Gets the origin platform. If this resource is created in eChempad it will be "eChempad".
     *
     * @return String that identifies the original platform.
     */
    String getOriginPlatform();

    /**
     * Sets the originPlatform.
     *
     * @param originPlatform String to identify the platform of origin of this DataEntity.
     */
    void setOriginPlatform(String originPlatform);

    /**
     * Gets the type that this resource has on the origin platform. If it comes from eChempad it will be null.
     *
     * @return The origin type as a String, it could be Journal, Document, Experiment or some other types.
     */
    String getOriginType();

    /**
     * Sets the type that this resource has on the origin platform.
     *
     * @param originType The original platform that this DataEntity came from.
     */
    void setOriginType(String originType);

    /**
     *     Since this is an external element that is present in Signals but we do
     *             * not know (yet) how to compute it, this field
     * Gets the value of digest of this element.
     *
     * @return The value of digest of this element. In elements coming from Signals this is a natural number.
     */
    String getDigest();

    /**
     * Sets the value of digest of this element.
     *
     * @param digest Value of digest.
     */
    void setDigest(String digest);

    /**
     * Gets the department that this element belongs to.
     *
     * @return Value of the department. It will be the surname of the group leader: Echavarren, Bo, Lopez...
     */
    String getDepartment();

    /**
     * Sets the department that this element belongs to.
     *
     * @param department Value of the department.
     */
    void setDepartment(String department);

    /**
     * The username that created this resource in the origin platform. It will be null for resources created at
     * eChempad.
     *
     * @return The username from the origin platform, as a String. It will be an email.
     */
    String getOriginOwnerUsername();

    /**
     * Sets the username of this resource in the origin platform.
     *
     * @param originOwnerUsername The username in the origin platform. It has to be an email.
     */
    void setOriginOwnerUsername(String originOwnerUsername);
}
