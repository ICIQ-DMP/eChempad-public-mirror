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
package org.ICIQ.eChempad.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.ICIQ.eChempad.entities.genericJPAEntities.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Class used to receive the data of the addDocument request because it contains metadata at the same time as a
 * multipart file which gives a lot of troubles.
 * <p>
 * This class will contain all the fields that are present in the Document class, so they can be mapped from this class
 * to the entity class, while transforming the multipart file type into a LOB type that we will store into the DB.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "typeName",
        defaultImpl = DocumentWrapper.class)
public class DocumentWrapper extends DataEntityImpl implements DataEntity {

    @JsonIgnore
    private MultipartFile file;

    public DocumentWrapper() {
        this.updateLastEditionDate();
        this.initCreationDate();
    }

    public DocumentWrapper(Document document) {
        this.setId(document.getId());
        this.setCreationDate(document.getCreationDate());
        this.setDepartment(document.getDepartment());
        this.setDescription(document.getDescription());
        this.setDigest(document.getDigest());
        this.setLastEditionDate(document.getLastEditionDate());
        this.setName(document.getName());
        this.setOriginCreationDate(document.getOriginCreationDate());
        this.setOriginId(document.getOriginId());
        this.setOriginLastEditionDate(document.getOriginLastEditionDate());
        this.setOriginOwnerUsername(document.getOriginOwnerUsername());
        this.setOriginPlatform(document.getOriginPlatform());
        this.setOriginCreationDate(document.getOriginCreationDate());
        this.setOriginType(document.getOriginType());
        this.setParent(document.getParent());
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public long getSize()
    {
        return this.file.getSize();
    }

    public String getOriginalFilename()
    {
        return this.file.getOriginalFilename();
    }

    public MediaType getContentType()
    {
        return MediaType.parseMediaType(Objects.requireNonNull(this.file.getContentType()));
    }

    /**
     * Implemented by every class to return its own type.
     *
     * @return Class of the object implementing this interface.
     */
    @Override
    public <T extends Entity> Class<T> getType() {
        return (Class<T>) DocumentWrapper.class;
    }

}