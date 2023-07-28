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
