package org.ICIQ.eChempad.entities;

import java.nio.file.Path;
import java.util.UUID;

/**
 * Extends the functionality of the abstract class Document for visualizing and manipulating CSV files
 */
public class DocumentCSV extends Document {

    /**
     * Constructor
     * @param name Name of the document (file)
     * @param path path to the file. It can be relative or absolute.
     */
    public DocumentCSV(Path path, String name, String description) {
        this.UUid = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.path = path;
    }


    /**
     * CSVs can be treated as text files, so we can call directly the method of the parent where we have the logic to
     * printing text files.
     * @return String that contains the whole file data.
     */
    @Override
    public String displayFile() {
        return super.displayFile();
    }



}
