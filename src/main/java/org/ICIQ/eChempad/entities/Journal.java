package org.ICIQ.eChempad.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Model class to ideally store Experiments that are related to the same project.
 *
 * A Journal contains many Experiment and some metadata (description, name). A Journal is the only shareable
 * structure with other users.
 */
@Entity
@Table(name="journals")
public class Journal {
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
    @Column(name = "id", nullable = false, updatable = false)
    private final UUID UUid;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", length = 1000, nullable = false)
    private String description;

    @OneToMany(targetEntity=Document.class, mappedBy="UUid", fetch=FetchType.EAGER)
    private List<Document> documents;

    /**
     * Constructor
     * @param name name used by humans to identify a certain Journal. No collision is expected from Journals with same
     *             name
     * @param description description of the content of the Journal and its Experiments.
     */
    public Journal(String name, String description) {
        this.UUid = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.documents = new ArrayList<>();
    }


    // GETTERS AND SETTERS

    public UUID getUUid() {
        return UUid;
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

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}



