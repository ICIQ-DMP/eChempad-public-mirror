/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2023 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.entities.genericJPAEntities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.ICIQ.eChempad.configurations.converters.MediaTypeConverter;
import org.springframework.http.MediaType;

import javax.persistence.*;
import java.sql.Blob;

/**
 * Class that contains the "leaves" of our tree-like structure. In the leaves we can find the {@code Document}s, a data
 * class that contains a file and relative metadata for that particular file.
 * <p>
 * Notice that {@code Document} is an entity that is only used to model the storage in the database. Other entities are
 * used to model the storage but also model the serialization and deserialization of the entity. This is due to the fact
 * that the {@code Document} class contains a binary data as a BLOB, which is not easy to serialize. So, instead, we use
 * the class {@code DocumentWrapper} as a serializable equivalent type to the type {@code Document}, that is used in the
 * transmission of data over the Internet.
 * @see "DocumentWrapper"
 * @see "DocumentWrapperConverter"
 */
@javax.persistence.Entity
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "typeName",
        defaultImpl = Document.class)
public class Document extends EntityImpl implements DataEntity {

    /**
     * Name of the file that is stored as a BLOB in this class. It can be the original name from the file that was
     * submitted with the {@code DocumentWrapper} in an "add" petition or manually set when using directly the service.
     */
    @Column(length = 1000)
    protected String originalFilename;

    /**
     * Media type of the file that is stored as a BLOB in this class. It can come from the original file that was
     * submitted with the {@code DocumentWrapper} in an "add" petition or manually set when using directly the service.
     */
    @Convert(converter = MediaTypeConverter.class)
    @Column(length = 1000, nullable = false)
    protected MediaType contentType;

    /**
     * Size of the file that is stored as a BLOB in this class. It could be transient, but we do not mark as is, since
     * we need to avoid possible extra connections with the database. Transient means that is a derived property from
     * another field, in this case from the file.
     */
    @Column
    protected long fileSize;

    /**
     * Reference to the {@code Experiment} that this {@code Document} is in. It could be null, that would mean that
     * this {@code Document} is not in any {@code Experiment}.
     */
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "parent_id",
            referencedColumnName = "id"
    )
    @JsonIgnore
    protected Container parent;

    /**
     * BLOB field. The class {@code Blob} wraps an {@code InputStream] that points to the database. Notice that you need
     * to manipulate the stream in order to read it, which means that in streaming-using operations the file will not be
     * loaded entirely in memory. If we would use the approach of using an array of {@code byte}s, the files that are
     * supplied or sent in each request would be entirely loaded in memory.
     * <p>
     * Notice also that in order to read the BLOB a session to the database must be present, so remember to explicitly
     * open or join one session to the database or use {@code @Transactional} to define the boundaries of your database
     * session in the methods.
     * <p>
     * Another thing to mention is that the {@code InputStream} of the {@code Blob} class is one-time read, so if you
     * consume it, you need to reload the instance from the database if you want to consume it again or an exception
     * will show up about rewinding the input stream.
     * <p>
     * @see DocumentWrapper
     * @see DocumentWrapperConverter
     */
    @JsonIgnore
    @Column
    @Lob
    @Basic(fetch = FetchType.EAGER)
    protected Blob blob;

    /**
     * Name of this {@code Document}.
     */
    @Column(length = 1000, nullable = false)
    protected String name;

    /**
     * Description of this {@code Document}.
     */
    @Column(length = 1000, nullable = false)
    protected String description;

    public Document() {}

    public Document(String name, String description) {
        this.name = name;
        this.description = description;
        this.initCreationDate();
    }


    @Override
    public String toString() {
        return "Document{" +
                "originalFilename='" + originalFilename + '\'' +
                ", contentType=" + contentType +
                ", fileSize=" + fileSize +
                ", parent=" + parent +
                ", blob=" + blob +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    /**
     * Implemented by every class to return its own type.
     *
     * @return Class of the object implementing this interface.
     */
    @Override
    public <T extends Entity> Class<T> getType() {
        return (Class<T>) Document.class;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

// GETTERS SETTERS AND TO-STRING

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public MediaType getContentType() {
        return contentType;
    }

    public void setContentType(MediaType contentType) {
        this.contentType = contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public Blob getBlob() {
        return blob;
    }

    public void setBlob(Blob blob) {
        this.blob = blob;
    }

    public Container getParent() {
        return parent;
    }

    public void setParent(Container parent) {
        this.parent = parent;
    }
}
