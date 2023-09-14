package org.ICIQ.eChempad.services.importServices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ICIQ.eChempad.entities.genericJPAEntities.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Service
public class SignalsAPIParsingServiceImpl implements SignalsAPIParsingService{

    @Override
    public Date parseDateFromJSON(ObjectNode metadataJSON)
    {
        String signalsJournalCreationDate = metadataJSON.get("data").get(0).get("attributes").get("createdAt").toString().replace("\"", "");
        return this.parseDate(signalsJournalCreationDate);
    }

    @Override
    public Date parseUpdateDateFromJSON(ObjectNode metadataJSON)
    {
        String signalsJournalCreationDate = metadataJSON.get("data").get(0).get("attributes").get("editedAt").toString().replace("\"", "");
        return this.parseDate(signalsJournalCreationDate);
    }

    @Override
    public Date parseDate(String dateData)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = dateFormat.parse(dateData);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Override
    public Container parseContainer(ObjectNode containerJSON)
    {
        return new Container(this.parseDataEntity(containerJSON));
    }

    @Override
    public Document parseDocument(ObjectNode documentJSON) {
        return new Document(this.parseDataEntity(documentJSON));
    }

    @Override
    public DataEntity parseDataEntity(ObjectNode dataEntityJSON) {
        DataEntity dataEntity = new DataEntityImpl() {
            @Override
            public <T extends Entity> Class<T> getType() {
                return (Class<T>) DataEntityImpl.class;
            }
        };

        // Remove quotes and obtain the EID of this entity.
        dataEntity.setOriginId(dataEntityJSON.get("data").get(0).get("id").toString().replace("\"", ""));

        // Parse root container name
        dataEntity.setName(dataEntityJSON.get("data").get(0).get("attributes").get("name").toString().replace("\"", ""));

        // Parse root container description
        dataEntity.setDescription(dataEntityJSON.get("data").get(0).get("attributes").get("description").toString().replace("\"", ""));

        // Parse root container creation date
        Date now = new Date();
        dataEntity.setCreationDate(now);

        // Parse root container creation date in origin
        dataEntity.setOriginCreationDate(this.parseDateFromJSON(dataEntityJSON));

        // Set local last edition date with the last edition date in origin
        dataEntity.setLastEditionDate(now);

        // Get origin type
        dataEntity.setOriginType(dataEntityJSON.get("data").get(0).get("attributes").get("type").toString().replace("\"", ""));

        // Parse root container last edition date in origin
        dataEntity.setOriginLastEditionDate(this.parseUpdateDateFromJSON(dataEntityJSON));

        // Set origin platform
        dataEntity.setOriginPlatform("Signals");

        // Parse digest value
        dataEntity.setDigest(dataEntityJSON.get("data").get(0).get("attributes").get("digest").toString().replace("\"", ""));

        // Parse entity type in origin
        dataEntity.setOriginType(dataEntityJSON.get("data").get(0).get("attributes").get("type").toString().replace("\"", ""));

        // Parse department
        JsonNode objectNode = dataEntityJSON.get("data").get(0).get("attributes").get("fields").get("Department");
        // If we do not have the department field set it as unknown
        if (objectNode == null)
        {
            dataEntity.setDepartment("Unknown");
        }
        else
        {
            dataEntity.setDepartment(objectNode.get("value").toString().replace("\"", ""));
        }

        // Username of the owner in origin
        /*
         I found out that it seems that when a journal is not owned by you, you can not get the included information
         and because of that the common way of parsing the owner username does not work
         */
        if (dataEntityJSON.get("included") == null)
        {
            dataEntity.setOriginOwnerUsername("Unknown");
        }
        else
        {
            dataEntity.setOriginOwnerUsername(dataEntityJSON.get("included").get(0).get("attributes").get("userName").toString().replace("\"", ""));
        }
        return dataEntity;
    }

    @Override
    public MultipartFile parseDocumentExport(String exportedDocumentEID, HttpHeaders httpHeadersOfRequest, InputStream inputStream) {
        MultipartFile multipartFile = null;
        MediaType contentType;
        if (httpHeadersOfRequest.getContentType() == null)
        {
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }
        else
        {
            contentType = httpHeadersOfRequest.getContentType();
        }

        try {
            multipartFile = new MockMultipartFile(exportedDocumentEID, httpHeadersOfRequest.getContentDisposition().getFilename(), contentType.toString(), inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return multipartFile;
    }
}
