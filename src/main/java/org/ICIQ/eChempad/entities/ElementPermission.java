package org.ICIQ.eChempad.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Model class to store, visualize and manage a Document (contained in a file).
 *
 * Each Document can be in different formats. Each class that inherits from this class extends its behaviour by adding
 * visualization methods for concrete types of document.
 */
@Entity
@Table(name="elementpermission", uniqueConstraints = {
        @UniqueConstraint(columnNames = "UUID")
})
public class ElementPermission<T> {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    //@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "UUID", nullable = false)
    protected UUID id;

    @Column(name = "resource", nullable = false)
    protected UUID resource;

    @Column(name = "type", length = 1000, nullable = false)
    protected Class<T> type;

    @Column(name = "role", length = 1000, nullable = false)
    protected Enum<Role> role;

    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JsonIgnore
    protected Researcher researcher;

    public ElementPermission() {}


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
        return type;
    }

    public void setType(Class type) {
        this.type = type;
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
}