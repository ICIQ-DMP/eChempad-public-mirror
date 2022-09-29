package org.ICIQ.eChempad.entities.genericJPAEntities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.ICIQ.eChempad.configurations.converters.UUIDConverter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.http.MediaType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

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

    @Id
    @Convert(converter = UUIDConverter.class)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(nullable = false, unique = true)
    protected UUID id;

    @Column(length = 1000, nullable = false)
    protected String name;

    @Column(length = 1000, nullable = false)
    protected String description;

    @Column(length = 1000, nullable = true)
    protected String originalFilename;

    @Column(length = 1000, nullable = false)
    protected MediaType contentType;

    @Column
    protected long fileSize;

    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = true
    )
    @JoinColumn(
            name = "experiment",
            referencedColumnName = "id",
            nullable = true)
    @JsonIgnore
    protected Experiment experiment;

    public Document() {}

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

}
