package org.ICIQ.eChempad.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Model class to store many Documents (files) ideally from a single chemistry assay.
 *
 * An Experiment is composed of many Documents (files) and some metadata (description, name).
 */
public class Experiment {
    private UUID UUid;
    private String name;
    private String description;
    private List<Document> documents;

    /**
     * Constructor
     * @param name Name used by humans to identify this Experiment. There are no possibility of collisions since we use
     *             the UUID to manage the experiments.
     * @param description String used to describe the contents of this experiment.
     */
    public Experiment(String name, String description) {
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
