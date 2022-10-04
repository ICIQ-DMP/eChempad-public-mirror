/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.entities.genericJPAEntities;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

/**
 * A Journal contains many Experiment and some metadata (description, name).
 */
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "typeName",
        defaultImpl = Journal.class)
public class Journal extends JPAEntityImpl {
    /*
     * https://stackoverflow.com/questions/45086957/how-to-generate-an-auto-uuid-using-hibernate-on-spring-boot/45087148
     * https://thorben-janssen.com/generate-uuids-primary-keys-hibernate/
     */

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(unique = true)
    private UUID id;

    @Column(length = 1000, nullable = false)
    private String name;

    @Column(length = 1000, nullable = false)
    private String description;

    @OneToMany(
            targetEntity = Experiment.class,
            mappedBy = "journal",
            fetch = FetchType.EAGER,
            orphanRemoval = true  // cascade = CascadeType.ALL  https://stackoverflow.com/questions/16898085/jpa-hibernate-remove-entity-sometimes-not-working
    )
    private Set<Experiment> experiments;

    public Journal() {}

    /**
     * Constructor
     * @param name name used by humans to identify a certain Journal. No collision is expected from Journals with same
     *             name
     * @param description description of the content of the Journal and its Experiments.
     */
    public Journal(String name, String description) {
        this.name = name;
        this.description = description;
        // this.experiments = new HashSet<>();
    }

    // GETTERS AND SETTERS

    @Override
    public Serializable getId() {
        return this.id;
    }

    @Override
    public void setId(Serializable s) {
        this.id = (UUID) s;
    }

    /**
     * Implemented by every class to return its own type.
     *
     * @return Class of the object implementing this interface.
     */
    @Override
    public <T extends JPAEntity> Class<T> getType() {
        return (Class<T>) Journal.class;
    }

    /**
     * Obtains the typeName, used by jackson to deserialize generics.
     *
     * @return Name of the class as string.
     */
    @Override
    public String getTypeName() {
        return this.getType().getName();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Experiment> getExperiments() {
        return experiments;
    }

    public void setExperiments(Set<Experiment> experiments) {
        this.experiments = experiments;
    }

    @Override
    public String toString() {
        return "Journal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}