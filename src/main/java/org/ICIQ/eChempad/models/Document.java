package org.ICIQ.eChempad.models;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Model class to store, visualize and manage a Document (contained in a file).
 *
 * Each Document can be in different formats. Each class that inherits from this class extends its behaviour by adding
 * visualization methods for concrete types of document.
 */
public abstract class Document {
    protected UUID UUid;
    protected String name;
    protected String description;
    protected Path path;

    /**
     * Default display of a File: By default an instance of a File is always considered as text.
     * @return String containing a representation for a generic document.
     */
    public String displayFile()
    {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(this.name);
        contentBuilder.append(" ");
        contentBuilder.append(this.description);
        try (Stream<String> stream = Files.lines(Paths.get(this.path.toString()), StandardCharsets.UTF_8))  {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
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

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
