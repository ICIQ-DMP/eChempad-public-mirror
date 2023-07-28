package org.ICIQ.eChempad.entities.genericJPAEntities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public abstract class DataEntityImpl extends EntityImpl implements DataEntity{
    /**
     * Name of this {@code Document}.
     */
    @Column(length = 1000, nullable = false)
    protected String name;

    /**
     * Description of this {@code Document}.
     */
    @Column(length = 1000, nullable = false)
    protected String description;

    /**
     * Reference to the {@code Experiment} that this {@code Document} is in. It could be null, that would mean that
     * this {@code Document} is not in any {@code Experiment}.
     */
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "parent_id"
    )
    @JsonIgnore
    protected Container parent;

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
}
