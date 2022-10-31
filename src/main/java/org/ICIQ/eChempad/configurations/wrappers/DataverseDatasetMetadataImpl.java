package org.ICIQ.eChempad.configurations.wrappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.configurationprocessor.json.JSONArray;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataverseDatasetMetadataImpl implements DataverseDatasetMetadata{

    ObjectNode json;

    public DataverseDatasetMetadataImpl() {
        Stream<String> lines = null;
        try{
            Path path = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("static/dataset-finch1.json")).toURI());
            lines = Files.lines(path);
            String data = lines.collect(Collectors.joining("\n"));

            ObjectMapper mapper = new ObjectMapper();
            this.json = (ObjectNode) mapper.readTree(data);
            return;
        }
        catch(IOException | URISyntaxException e)
        {
            e.printStackTrace();
        } finally {
            assert lines != null;
            lines.close();
        }
        this.json = null;
    }


    @Override
    public void setTitle(String title) {
        ((ObjectNode) this.json.with("datasetVersion").with("metadataBlocks").with("citation").withArray("fields").get(0)).put("value", title);
    }

    @Override
    public void setAuthorName(String authorName) {
        ((ObjectNode) ((ObjectNode) this.json.with("datasetVersion").with("metadataBlocks").with("citation").withArray("fields").get(1)).withArray("value").get(0)).with("authorName").put("value", authorName);
    }

    @Override
    public void setAuthorAffiliation(String authorAffiliation) {
        ((ObjectNode) ((ObjectNode) this.json.with("datasetVersion").with("metadataBlocks").with("citation").withArray("fields").get(1)).withArray("value").get(0)).with("authorAffiliation").put("value", authorAffiliation);
    }

    @Override
    public void setContactEmail(String contactEmail) {
        ((ObjectNode) ((ObjectNode) this.json.with("datasetVersion").with("metadataBlocks").with("citation").withArray("fields").get(2)).withArray("value").get(0)).with("datasetContactEmail").put("value", contactEmail);
    }

    @Override
    public void setDatasetContactName(String datasetContactName) {
        ((ObjectNode) ((ObjectNode) this.json.with("datasetVersion").with("metadataBlocks").with("citation").withArray("fields").get(2)).withArray("value").get(0)).with("datasetContactName").put("value", datasetContactName);

    }

    @Override
    public void setDescription(String description) {
        ((ObjectNode) ((ObjectNode) this.json.with("datasetVersion").with("metadataBlocks").with("citation").withArray("fields").get(3)).withArray("value").get(0)).with("dsDescriptionValue").put("value", description);
    }

    @Override
    public void setSubjects(List<String> categories) {
        ArrayNode array = new ObjectMapper().createArrayNode();

        for (String category : categories)
        {
            array.add(category);
        }
        ((ObjectNode) this.json.with("datasetVersion").with("metadataBlocks").with("citation").withArray("fields").get(4)).set("value", array);
    }

    public void setJson(ObjectNode json) {
        this.json = json;
    }

    public ObjectNode getJson() {
        return json;
    }

    @Override
    public String toString() {
        return json.toPrettyString();
    }
}
