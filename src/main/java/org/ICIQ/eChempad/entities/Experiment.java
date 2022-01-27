package org.ICIQ.eChempad.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

/**
 * Model class to store many Documents (files) from a single chemistry assay.
 */
@Entity
@Table(name="Experiment", uniqueConstraints = {
        @UniqueConstraint(columnNames = "UUID")
})
public class Experiment{
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "UUID")
    private UUID id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", length = 1000, nullable = false)
    private String description;

    @OneToMany(
            targetEntity = Document.class,
            mappedBy = "experiment",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
)
    private Set<Document> documents;

    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JoinColumn(
            name = "journal_id",
            nullable = false)
    private Journal journal;

    public Experiment() {}

    /**
     * Constructor
     * @param name Name used by humans to identify this Experiment. There are no possibility of collisions since we use
     *             the UUID to manage the experiments.
     * @param description String used to describe the contents of this experiment.
     */
    public Experiment(String name, String description) {
        this.name = name;
        this.description = description;
        this.documents = new HashSet<>();
    }


    // GETTERS AND SETTERS
    public UUID getUUid() {
        return this.id;
    }

    public void setUUid(UUID s) {
        this.id = s;
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
}
