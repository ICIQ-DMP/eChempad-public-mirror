/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Model class to store, visualize and manage a Document (contained in a file).
 *
 * Each Document can be in different formats. Each class that inherits from this class extends its behaviour by adding
 * visualization methods for concrete types of document.
 */
@Entity
@Table(name="document", uniqueConstraints = {
        @UniqueConstraint(columnNames = "UUID")
})
public class Document implements IEntity{
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )

    @Column(name = "UUID")
    protected UUID id;

    @Column(name = "name", length = 1000, nullable = false)
    protected String name;

    @Column(name = "original_filename", length = 1000, nullable = true)
    protected String originalFilename;

    @Column(name = "description", length = 1000, nullable = false)
    protected String description;

    @Column(name = "content_type", length = 1000, nullable = true)
    protected MediaType contentType;

    @Column(name = "file_size", length = 1000, nullable = true)
    protected long fileSize;

    @Column(name = "file", length = 1000, nullable = true)
    protected String path;

    @OneToMany(
            targetEntity = ElementPermission.class,
            mappedBy = "document",
            fetch = FetchType.EAGER,
            orphanRemoval = true  // cascade = CascadeType.ALL  https://stackoverflow.com/questions/16898085/jpa-hibernate-remove-entity-sometimes-not-working
    )
    @Nullable
    @JsonIgnore
    @JsonManagedReference
    private Set<ElementPermission> permissions = new HashSet<>();

    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JoinColumn(
            name = "experiment_id",
            nullable = false)
    @JsonIgnore
    protected Experiment experiment;

    public Document() {}

    public Document(String name, String description) {
        this.name = name;
        this.description = description;
        this.experiment = null;
        this.permissions = new HashSet<>();
    }

    public Document(String name, String description, Experiment experiment, MediaType contentType) {
        this.name = name;
        this.description = description;
        this.experiment = experiment;
        this.permissions = new HashSet<>();
        this.contentType = contentType;
    }



    // GETTERS AND SETTERS

    public UUID getUUid() {
        return this.id;
    }

    public void setUUid(UUID s) {
        this.id = s;
    }

    @Override
    public boolean isContainer(UUID entity_uuid) {
        return false;
    }

    @Override
    public boolean isContained(UUID entity_uuid) {
        if (this.experiment.getUUid().equals(entity_uuid))
        {
            return true;
        }
        else
        {
            return (this.experiment.isContained(entity_uuid));
        }
    }

    @Override
    public Class<?> getMyType() {
        return Document.class;
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

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    @JsonIgnore
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Nullable
    public Set<ElementPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(@Nullable Set<ElementPermission> permissions) {
        this.permissions = permissions;
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

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }
}
