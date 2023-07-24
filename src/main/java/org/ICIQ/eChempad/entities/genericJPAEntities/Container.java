package org.ICIQ.eChempad.entities.genericJPAEntities;


import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A Journal contains many Experiment and some metadata (description, name, creation date...). A journal is an entity,
 * and as such, the class contains annotations on how data types are treated when interacting with the corresponding
 * table in the database.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 10/10/2022
 * @see <a href="https://stackoverflow.com/questions/45086957/how-to-generate-an-auto-uuid-using-hibernate-on-spring-boot/45087148">...</a>
 * @see <a href="https://thorben-janssen.com/generate-uuids-primary-keys-hibernate/">...</a>
 */
@javax.persistence.Entity
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "typeName",
        defaultImpl = Container.class)
public class Container extends EntityImpl implements DataEntity {

    @ManyToOne(cascade={CascadeType.ALL},
            fetch = FetchType.EAGER
    )
    private Container parent;

    @OneToMany(fetch = FetchType.EAGER, mappedBy="parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Container> childrenContainers = new HashSet<Container>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy="parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Document> childrenDocuments = new HashSet<Document>();

    @Override
    public <T extends Entity> Class<T> getType() {
        return (Class<T>) Container.class;
    }

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

    public Container() {}

    /**
     * Constructor
     * @param name name used by humans to identify a certain Journal. No collision is expected from Journals with same
     *             name
     * @param description description of the content of the Journal and its Experiments.
     */
    public Container(String name, String description) {
        this.name = name;
        this.description = description;
        this.initCreationDate();
    }

    @Override
    public String toString() {
        return "Container{" +
                ", childrenContainers=" + childrenContainers +
                ", childrenDocuments=" + childrenDocuments +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    // GETTERS AND SETTERS
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

    public Container getParent() {
        return parent;
    }

    public void setParent(Container parent) {
        this.parent = parent;
    }

    public Set<Container> getChildrenContainers() {
        return childrenContainers;
    }

    public void setChildrenContainers(Set<Container> childrenContainers) {
        this.childrenContainers = childrenContainers;
    }

    public Set<Document> getChildrenDocuments() {
        return childrenDocuments;
    }

    public void setChildrenDocuments(Set<Document> childrenDocuments) {
        this.childrenDocuments = childrenDocuments;
    }
}