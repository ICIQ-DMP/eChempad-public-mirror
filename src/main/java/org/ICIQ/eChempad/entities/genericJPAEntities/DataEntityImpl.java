package org.ICIQ.eChempad.entities.genericJPAEntities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Date;

@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public abstract class DataEntityImpl extends EntityImpl implements DataEntity{

    /**
     * Reference to the {@code Container} that this {@code DataEntity} is in. It could be null, that would mean that
     * this {@code DataEntity} is not in any {@code Container}, which means that this would be a root container.
     */
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "parent_id"
    )
    @JsonIgnore
    protected Container parent;

    /**
     * Name of this {@code DataEntity}.
     */
    @Column(length = 1000, nullable = false)
    protected String name;

    /**
     * Description of this {@code DataEntity}.
     */
    @Column(length = 1000, nullable = false)
    protected String description;

    /**
     * Date of creation of the entity. Access with read only allow us to ignore the creation date field when doing post
     * (parsing JSON) but including it when we do gets.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(nullable = false)
    private Date creationDate;

    /**
     * Special method to initialize the creation date, which has to be the current time when executing this code.
     */
    public void initCreationDate() {
        this.creationDate = new Date();
    }

    // SETTERS GETTERS AND TO STRINGS
    public Container getParent() {
        return parent;
    }

    public void setParent(Container parent) {
        this.parent = parent;
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

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
