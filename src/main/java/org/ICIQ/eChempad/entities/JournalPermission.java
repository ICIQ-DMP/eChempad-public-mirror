package org.ICIQ.eChempad.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

/**
 * Used to store information about the User and its workspace.
 *
 * It has a list containing the different Journal that conform the workspace.
 */
@Entity
@Table(name="JournalPermission", uniqueConstraints = {
        @UniqueConstraint(columnNames = "UUID")
})
public class JournalPermission {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "UUID")
    private UUID uuid;


    @OneToMany(
            targetEntity = ExperimentPermission.class,
            mappedBy = "experiment",
            fetch = FetchType.EAGER,
            // If a journal permission is deleted, all of the experiment permissions have to be deleted
            orphanRemoval = true  // cascade = CascadeType.ALL  https://stackoverflow.com/questions/16898085/jpa-hibernate-remove-entity-sometimes-not-working
    )
    private Set<ExperimentPermission> experiments;


    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JoinColumn(
            name = "researcher_id",
            nullable = false)
    private Researcher researcher;


    @Column(name = "permission", nullable = false)
    private Permission permission;


    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JoinColumn(
            name = "journal_id",
            nullable = false)
    private Journal journal;



    public JournalPermission() {}

    public JournalPermission(Set<ExperimentPermission> experiments, Researcher researcher, Permission permission) {
        this.experiments = experiments;
        this.researcher = researcher;
        this.permission = permission;
    }



    public UUID getUuid() {
        return uuid;
    }

    public Set<ExperimentPermission> getExperiments() {
        return experiments;
    }

    public void setExperiments(Set<ExperimentPermission> experiments) {
        this.experiments = experiments;
    }

    public Researcher getResearcher() {
        return researcher;
    }

    public void setResearcher(Researcher researcher) {
        this.researcher = researcher;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }
}
