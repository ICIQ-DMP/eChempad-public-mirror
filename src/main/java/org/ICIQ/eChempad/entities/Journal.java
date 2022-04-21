/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

/**
 * Model class to ideally store Experiments that are related to the same project.
 *
 * A Journal contains many Experiment and some metadata (description, name). A Journal is the only shareable
 * structure with other users.
 */
@Entity
@Table(name="Journal", uniqueConstraints = {
        @UniqueConstraint(columnNames = "UUID")
})
public class Journal implements IEntity{
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
    @Column(name = "UUID")
    private UUID id;


    @Column(name = "name", length = 1000, nullable = false)
    private String name;


    @Column(name = "description", length = 1000, nullable = false)
    private String description;


    @OneToMany(
            targetEntity = Experiment.class,
            mappedBy = "journal",
            fetch = FetchType.EAGER,
            orphanRemoval = true  // cascade = CascadeType.ALL  https://stackoverflow.com/questions/16898085/jpa-hibernate-remove-entity-sometimes-not-working
)
    private Set<Experiment> experiments;


    @OneToMany(
            targetEntity = ElementPermission.class,
            mappedBy = "id",
            fetch = FetchType.EAGER,
            orphanRemoval = true  // cascade = CascadeType.ALL  https://stackoverflow.com/questions/16898085/jpa-hibernate-remove-entity-sometimes-not-working
    )
    private Set<ElementPermission> permissions;

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
        this.experiments = new HashSet<>();
        this.permissions = new HashSet<>();
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
        // Search UUID for the inner experiment
        if (this.getExperiments().stream().anyMatch(experiment -> experiment.getUUid().equals(entity_uuid)))
        {
            // Return true if there is match
            return true;
        }
        else  // If we can find it in the immediate level, we ask the next level
        {
            return this.getExperiments().stream().anyMatch(experiment -> experiment.isContainer(entity_uuid));
        }
    }

    @Override
    public boolean isContained(UUID entity_uuid) {
        return false;
    }

    @Override
    public Class<Journal> getMyType() { return Journal.class; }

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
        return this.experiments;
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
                ", experiments=" + experiments +
                '}';
    }

    public Set<ElementPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<ElementPermission> permissions) {
        this.permissions = permissions;
    }
}



