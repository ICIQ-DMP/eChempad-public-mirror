package org.ICIQ.eChempad.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

/**
 * Model class to store many Documents (files) from a single chemistry assay.
 */
@Entity
@Table(name="ExperimentPermission", uniqueConstraints = {
        @UniqueConstraint(columnNames = "UUID")
})
public class ExperimentPermission {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "UUID")
    private UUID id;


    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JoinColumn(
            name = "experiment_id",
            nullable = false)
    private Experiment experiment;


    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JoinColumn(
            name = "journal_id",
            nullable = false)
    private JournalPermission journal;


    // Can be null because it is only to override journal permissions
    @Column(name="permission", length = 1000, nullable = true)
    private Permission permission;


    public ExperimentPermission() {}

    public UUID getId() {
        return id;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public JournalPermission getJournal() {
        return journal;
    }

    public void setJournal(JournalPermission journal) {
        this.journal = journal;
    }
}
