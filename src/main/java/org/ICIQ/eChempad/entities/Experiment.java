package org.ICIQ.eChempad.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.security.Permission;
import java.util.*;

/**
 * Model class to store many Documents (files) from a single chemistry assay.
 */
@Entity
@Table(name="Experiment", uniqueConstraints = {
        @UniqueConstraint(columnNames = "UUID")
})
public class Experiment implements IEntity{
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
            targetEntity = Document.class,
            mappedBy = "experiment",
            fetch = FetchType.EAGER,
            orphanRemoval = true  // cascade = CascadeType.ALL  https://stackoverflow.com/questions/16898085/jpa-hibernate-remove-entity-sometimes-not-working
)
    @JsonIgnore
    private Set<Document> documents;

    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JoinColumn(
            name = "journal_id",
            nullable = false)
    @JsonIgnore
    private Journal journal;



    @OneToMany(
            targetEntity = ElementPermission.class,
            mappedBy = "id",
            fetch = FetchType.EAGER,
            orphanRemoval = true  // cascade = CascadeType.ALL  https://stackoverflow.com/questions/16898085/jpa-hibernate-remove-entity-sometimes-not-working
    )
    @NotNull
    @JsonIgnore
    private Set<ElementPermission> permissions = new HashSet<>();

    public Experiment() {}

    /**
     * Constructor
     * @param name Name used by humans to identify this Experiment. There are no possibility of collisions since we use
     *             the UUID to manage the experiments.
     * @param description String used to describe the contents of this experiment.
     */
    public Experiment(String name, String description, Journal journal) {
        this.name = name;
        this.description = description;
        this.documents = new HashSet<>();
        this.journal = journal;
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
    public Class<?> getMyType() {
        return Experiment.class;
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

    public Set<Document> getDocuments() {
        return this.documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

    public Journal getJournal() {
        return journal;
    }

    public void setJournal(Journal journal) {
        this.journal = journal;
    }

    public @NotNull Set<ElementPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(@NotNull Set<ElementPermission> permissions) {
        this.permissions = permissions;
    }
}

