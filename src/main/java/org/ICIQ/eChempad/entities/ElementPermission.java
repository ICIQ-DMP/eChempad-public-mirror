package org.ICIQ.eChempad.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ICIQ.eChempad.EChempadApplication;
import org.ICIQ.eChempad.repositories.GenericRepositoryClass;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Model class to store the permission level / authority that a researcher has against a concrete resource of a certain
 * type. It should be used before reaching the service level, so it is used in the filters before actually calling the
 * method.
 */
@Entity
@Table(name="elementpermission", uniqueConstraints = {
        @UniqueConstraint(columnNames = "UUID")
})
public class ElementPermission implements IEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "UUID", nullable = false)
    protected UUID id;




    // Resource that we are limiting access to. It cannot be mapped to the DB since it is dynamically bind depending on
    // the class that is implementing IEntity
    //@JsonIgnore
    //protected IEntity resource;

    // Type of resource we are limiting; used to know in which table we need to check the IEntity resource.
    // This only makes sense with Hibernate since fields have dual format: one serialized for the DB and binarized for
    // Java code.
    @Column(name = "type", length = 100, nullable = false)
    protected Class<?> type;


    // Authority that this user has against this resource.
    @Column(name = "authority", length = 100, nullable = false)
    protected Authority authority;


    // https://stackoverflow.com/questions/4121485/columns-not-allowed-on-a-manytoone-property
    @JoinColumn(name = "researcher")
    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JsonIgnore
    protected Researcher researcher;


    // Only one resource is not null, the other ones have to be null. One element per element permission
    // {

    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = true
    )
    @JoinColumn(
            name = "experiment_id",
            nullable = true)
    @JsonIgnore
    @Nullable
    protected Experiment experiment;


    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = true
    )
    @JoinColumn(
            name = "journal_id",
            nullable = true)
    @JsonIgnore
    @Nullable
    protected Journal journal;




    // }


    public ElementPermission() {}

    public ElementPermission(IEntity resource, Authority authority, Researcher researcher)
    {
        this.type = resource.getClass();
        if (resource.getMyType().equals(Journal.class))
        {
            this.journal = (Journal) resource;
        }
        else if (resource.getMyType().equals(Experiment.class))
        {
            this.experiment = (Experiment) resource;
        }
        else
        {
            LoggerFactory.getLogger(EChempadApplication.class).info("AFTER");

        }
        this.authority = authority;
        this.researcher = researcher;
    }


    public <T extends IEntity> T getElement()
    {
        if (this.type.equals(Experiment.class))
        {
            return (T) this.experiment;
        }
        else if (this.type.equals(Journal.class))
        {
            return (T) this.journal;
        }
        else
        {
            // TODO: make error
            return null;
        }
    }

    // Getters and setters


    public UUID getUUid() {
        return id;
    }

    public void setUUid(UUID id) {
        this.id = id;
    }

    @Override
    public Class<?> getMyType() {
        return ElementPermission.class;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Enum<Authority> getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public Researcher getResearcher() {
        return researcher;
    }

    public void setResearcher(Researcher researcher) {
        this.researcher = researcher;
    }


    @Override
    public String toString() {
        return "ElementPermission{" +
                "id=" + id +
                ", type=" + type +
                ", authority=" + authority +
                ", researcher=" + researcher.getEmail() +
                ", experiment=" + experiment.getUUid() +
                ", journal=" + journal.getUUid() +
                '}';
    }

    @Nullable
    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(@Nullable Experiment experiment) {
        this.experiment = experiment;
    }

    @Nullable
    public Journal getJournal() {
        return journal;
    }

    public void setJournal(@Nullable Journal journal) {
        this.journal = journal;
    }


}