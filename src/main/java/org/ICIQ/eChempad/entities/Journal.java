package org.ICIQ.eChempad.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * Model class to ideally store Experiments that are related to the same project.
 *
 * A Journal contains many Experiment and some metadata (description, name). A Journal is the only shareable
 * structure with other users.
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
public class Journal extends GenericEntity implements IEntity{
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

    /* @OneToMany(
            targetEntity = Experiment.class,
            mappedBy = "journal",
            fetch = FetchType.EAGER,
            orphanRemoval = true  // cascade = CascadeType.ALL  https://stackoverflow.com/questions/16898085/jpa-hibernate-remove-entity-sometimes-not-working
    )
    private Set<Experiment> experiments;
    */

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

    public Serializable getId() {
        return this.id;
    }

    public void setId(Serializable s) {
        this.id = (UUID) s;
    }

    /**
     * Implemented by every class to return its own type, except for element permission, which returns the type of the
     * element that is giving permissions to.
     *
     * @return Class of the object implementing this interface.
     */
    @Override
    public <T extends IEntity> Class<T> getType() {
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

    /*
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

    */

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


    @Override
    public String toString() {
        return "Journal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}