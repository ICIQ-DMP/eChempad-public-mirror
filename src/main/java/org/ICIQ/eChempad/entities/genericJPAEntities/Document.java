package org.ICIQ.eChempad.entities.genericJPAEntities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.ICIQ.eChempad.configurations.converters.MediaTypeConverter;
import org.ICIQ.eChempad.configurations.converters.UUIDConverter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.http.MediaType;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Blob;
import java.util.UUID;

/**
 * Class that contains the "leaves" of our tree-like structure. In the leaves we can find the {@code Document}s, a data
 * class that contains a file and relative metadata for that particular file.
 *
 * Notice that {@code Document} is an entity that is only used to model the storage in the database. Other entities are
 * used to model the storage but also model the serialization and deserialization of the entity. This is due to the fact
 * that the {@code Document} class contains a binary data as a BLOB, which is not easy to serialize. So, instead, we use
 * the class {@code DocumentWrapper} as a serializable equivalent type to the type {@code Document}, that is used in the
 * transmission of data over the Internet.
 * @see "DocumentWrapper"
 * @see "DocumentWrapperConverter"
 */
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "typeName",
        defaultImpl = Document.class)
public class Document extends JPAEntityImpl{

    /**
     * Identity of the instance when is stored in database.
     */
    @Id
    @Convert(converter = UUIDConverter.class)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(nullable = false, unique = true)
    protected UUID id;

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
            name = "experiment",
            referencedColumnName = "id"
    )
    @JsonIgnore
    protected Experiment experiment;

    /**
     * BLOB field. The class {@code Blob} wraps an {@code InputStream] that points to the database. Notice that you need
     * to manipulate the stream in order to read it, which means that in streaming-using operations the file will not be
     * loaded entirely in memory. If we would use the approach of using an array of {@code byte}s, the files that are
     * supplied or sent in each request would be entirely loaded in memory.
     *
     * Notice also that in order to read the BLOB a session to the database must be present, so remember to explicitly
     * open or join one session to the database or use {@code @Transactional} to define the boundaries of your database
     * session in the methods.
     *
     * Another thing to mention is that the {@code InputStream} of the {@code Blob} class is one-time read, so if you
     * consume it, you need to reload the instance from the database if you want to consume it again or an exception
     * will show up about rewinding the input stream.
     *
     * @see DocumentWrapper
     * @see DocumentWrapperConverter
     */
    @JsonIgnore
    @Column
    @Lob
    @Basic(fetch = FetchType.EAGER)
    protected Blob blob;

    public Document() {}

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", originalFilename='" + originalFilename + '\'' +
                ", contentType=" + contentType +
                ", fileSize=" + fileSize +
                ", experiment=" + experiment +
                ", blob=" + blob +
                '}';
    }

    /**
     * Exposes and returns the UUID of an entity.
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
     * @param id ID that will be set. Only usable on detached spring boot instances
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
        return (Class<T>) Document.class;
    }

    // GETTERS / SETTERS

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public Blob getBlob() {
        return blob;
    }

    public void setBlob(Blob blob) {
        this.blob = blob;
    }
}
