package org.ICIQ.eChempad.entities;

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


    @Column(name = "name", length = 100, nullable = false)
    private String name;


    @Column(name = "description", length = 1000, nullable = false)
    private String description;


    @OneToMany(
            targetEntity = Experiment.class,
            mappedBy = "journal",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
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
        this.experiments = new HashSet<>();
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

    public Set<Experiment> getExperiments() {
        return this.experiments;
    }

    public void setExperiments(Set<Experiment> experiments) {
        this.experiments = experiments;
    }


}



