package org.ICIQ.eChempad.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ICIQ.eChempad.EChempadApplication;
import org.ICIQ.eChempad.repositories.GenericRepositoryClass;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.slf4j.LoggerFactory;

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


    // Resource that we are limiting access to
    @Column(name = "resource", nullable = false)
    protected IEntity resource;

    // Type of resource we are limiting; used to know in which table we need to check the IEntity resource.
    // This only makes sense with Hibernate since fields have dual format: one serialized for the DB and binarize for
    // Java code.
    @Column(name = "type", length = 100, nullable = false)
    protected Class<?> type;


    @Column(name = "authority", length = 100, nullable = false)
    protected Enum<Authority> authority;


    // https://stackoverflow.com/questions/4121485/columns-not-allowed-on-a-manytoone-property
    @JoinColumn(name = "researcher")
    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JsonIgnore
    protected Researcher researcher;



    public ElementPermission() {}

    public ElementPermission(IEntity resource, Authority authority, Researcher researcher)
    {
        this.type = resource.getClass();
        this.resource = resource;
        this.authority = authority;
        this.researcher = researcher;
    }


    // Getters and setters


    public UUID getUUid() {
        return id;
    }

    public void setUUid(UUID id) {
        this.id = id;
    }

    public IEntity getResource() {
        return resource;
    }

    public void setResource(IEntity resource) {
        this.resource = resource;
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

    public void setAuthority(Enum<Authority> authority) {
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
                ", resource=" + resource +
                ", type=" + type +
                ", authority=" + authority +
                ", researcher=" + researcher +
                '}';
    }
}