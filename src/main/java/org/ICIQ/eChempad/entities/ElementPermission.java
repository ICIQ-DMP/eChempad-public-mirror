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

    // Type of resource we are limiting; used to know in which table we need to check
    @Column(name = "type", length = 100, nullable = false)
    protected Class type;

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

    public ElementPermission(IEntity resource, Role role, Researcher researcher)
    {
        if (resource != null)
        {
            this.resource = resource.getUUid();
            // Obtain the actual type of this IEntity object. //RF not working, returns Object
            this.type = resource.getClass().getGenericSuperclass().getTypeName();
        }
        else
        {
            this.resource = null;
            this.type = null;
        }

        this.role = role;
        this.researcher = researcher;

        // Construct authority with the role + the ID of the resource that we are authorizing.
        if (this.resource == null)
        {
            this.authority = this.role.name();
        }
        else
        {
            this.authority = this.role + "_" + this.resource;
        }
    }

    public ElementPermission(IEntity resource, String role, Researcher researcher)
    {
        if (resource != null)
        {
            this.resource = resource.getUUid();
            // Obtain the actual type of this IEntity object. //RF not working, returns Object
            this.type = resource.getClass().getGenericSuperclass().getTypeName();
        }
        else
        {
            this.resource = null;
            this.type = null;
        }

        this.role = Role.valueOf(role);
        this.researcher = researcher;

        // Construct authority with the role + the ID of the resource that we are authorizing.
        if (this.resource == null)
        {
            this.authority = this.role.name();
        }
        else
        {
            this.authority = this.role + "_" + this.resource;
        }
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public UUID getResource() {
        return resource;
    }

    public void setResource(UUID resource) {
        this.resource = resource;
    }

    public Class getType() {
        try {
            return Class.forName(this.type);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setType(Class type) {
        this.type = type.getName();
    }

    public Enum<Role> getRole() {
        return role;
    }

    public void setRole(Enum<Role> role) {
        this.role = role;
    }

    public Researcher getResearcher() {
        return researcher;
    }

    public void setResearcher(Researcher researcher) {
        this.researcher = researcher;
    }

    @Override
    public UUID getUUid() {
        return this.id;
    }

    @Override
    public void setUUid(UUID id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ElementPermission{" +
                "id=" + id +
                ", resource=" + resource +
                ", type='" + type + '\'' +
                ", role=" + role +
                ", authority='" + authority + '\'' +
                ", researcher=" + researcher.getEmail() +
                '}';
    }
}