package org.ICIQ.eChempad.configurations;


import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;

/**
 * Class used to receive the data of the addDocument request because it contains metadata at the same time as a
 * multipart file which gives a lot of troubles.
 *
 * This class will contain all the fields that are present in the Document class so they can be mapped from this class
 * to the entity class, while transforming the multipart file type into a path type that we will store into the DB which
 * points to a file that have to be stolen on the File DB.
 */
public class DocumentHelper {
    private String name;

    private String description;

    private MultipartFile file;

    public DocumentHelper() {
    }

    public DocumentHelper(String name, String description, MultipartFile file) {
        this.name = name;
        this.description = description;
        this.file = file;
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

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}


