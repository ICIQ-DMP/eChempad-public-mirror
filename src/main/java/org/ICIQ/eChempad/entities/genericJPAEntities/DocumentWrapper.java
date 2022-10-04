package org.ICIQ.eChempad.entities.genericJPAEntities;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Class used to receive the data of the addDocument request because it contains metadata at the same time as a
 * multipart file which gives a lot of troubles.
 *
 * This class will contain all the fields that are present in the Document class, so they can be mapped from this class
 * to the entity class, while transforming the multipart file type into a LOB type that we will store into the DB.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "typeName",
        defaultImpl = DocumentWrapper.class)
public class DocumentWrapper extends JPAEntityImpl {

    private UUID id;

    private String name;

    private String description;

    private MultipartFile file;

    public DocumentWrapper() {
    }

    public DocumentWrapper(String name, String description, MultipartFile file) {
        this.name = name;
        this.description = description;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public String toString() {
        return "DocumentHelper{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", file={" +
                " name: " + file.getName() +
                " originalName: " + file.getOriginalFilename() +
                " contentType: " + file.getContentType() +
                "}" +
                '}';
    }

    /**
     * Exposes and returns the UUID of an entity.
     *
     * @return UUID of the entity.
     */
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
     * @param id ID that will be set. Only usable on dettached spring boot instances
     */
    @Override
    public void setId(Serializable id) {
        this.id = (UUID) id;
    }

    /**
     * Implemented by every class to return its own type.
     *
     * @return Class of the object implementing this interface.
     */
    @Override
    public <T extends JPAEntity> Class<T> getType() {
        return (Class<T>) DocumentWrapper.class;
    }
}