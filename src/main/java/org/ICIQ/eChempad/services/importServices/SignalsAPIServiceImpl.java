package org.ICIQ.eChempad.services.importServices;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Objects;

@Service("SignalsAPIService")
public class SignalsAPIServiceImpl implements SignalsAPIService{

    /**
     * The base URL of the Signals API.
     */
    static final String baseURL = "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0";

    private final WebClient webClient;

    @Autowired
    public SignalsAPIServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public ByteArrayResource exportDocumentFile(String APIKey, String document_eid, HttpHeaders receivedHeaders) throws IOException {

        String url = SignalsAPIServiceImpl.baseURL + "/entities/" + document_eid + "/export";

        ResponseEntity<ByteArrayResource> responseEntity = this.webClient.get()
                .uri(url)
                .header("x-api-key", APIKey)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .toEntity(ByteArrayResource.class)
                .block();

        // We are using the headers parameter of the function as an output parameter. But remember that in Java
        // everything is a pointer but args in functions are read-only. So we can modify the inside of the object but
        // not make the pointer go to another location by pointing to another object.
        if (responseEntity != null) {
            for (String headerKey: responseEntity.getHeaders().keySet())
            {
                receivedHeaders.put(headerKey, Objects.requireNonNull(responseEntity.getHeaders().get(headerKey)));
            }

            // In the cases where there is stored an empty file in Signals we receive a nullPointer instead of a ByteArrayResource empty
            if (responseEntity.getBody() == null)
            {
                return new ByteArrayResource("".getBytes());
            }
            else
            {
                return responseEntity.getBody();
            }
        }
        else
        {
            throw new IOException("Signals server returned null response");
        }
    }

    @Override
    public ObjectNode getEntityWithEUID(String APIKey, String journalEUID)
    {
        return this.webClient.get()
                .uri(SignalsAPIServiceImpl.baseURL + "/entities/" + journalEUID + "?include=owner")
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }



    @Override
    public ObjectNode getJournalWithOffset(String APIKey, int pageOffset)
    {
        return this.webClient.get()
                .uri(SignalsAPIServiceImpl.baseURL + "/entities?page[offset]=" + pageOffset + "&page[limit]=1&includeTypes=journal&include=owner") // &includeOptions=mine
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }

    @Override
    public ObjectNode getChildFromContainer(String APIKey, int pageOffset, String container_eid)
    {
        return this.webClient.get()
                .uri(SignalsAPIServiceImpl.baseURL + "/entities/" + container_eid + "/children?page[offset]=" + pageOffset + "&page[limit]=1&include=children%2C%20owner")
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }

    // OLD METHODS
    @Override
    public ObjectNode getDocumentFromExperiment(String APIKey, int pageOffset, String experiment_eid)
    {
        return this.webClient.get()
                .uri(SignalsAPIServiceImpl.baseURL + "/entities/" + experiment_eid + "/children?page[offset]=" + pageOffset + "&page[limit]=1&include=children%2C%20owner")
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }

    @Override
    public ObjectNode getUserData(String APIKey, int userNumber)
    {
        return this.webClient.get()
                .uri(SignalsAPIServiceImpl.baseURL + "/users/" + userNumber)
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }
}
