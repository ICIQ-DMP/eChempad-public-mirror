package org.ICIQ.eChempad.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ICIQ.eChempad.entities.Researcher;
import org.junit.jupiter.api.Assertions;
//import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.PKIXParameters;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

@Service
@ConfigurationProperties(prefix = "signals")
public class SignalsImportServiceClass implements SignalsImportService {

    @Value("${signals.baseURL}")
    private String baseURL;

    private final SecurityService securityService;

    public SignalsImportServiceClass(SecurityService securityService) {
        this.securityService = securityService;
    }


    /**
     * curl -X 'GET' \
     *   'https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/entities?page%5Boffset%5D=0&page%5Blimit%5D=1&includeTypes=journal' \
     *   -H 'accept: application/vnd.api+json' \
     *
     *
     *
     * https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/entities?page%5Boffset%5D=0&page%5Blimit%5D=1&includeTypes=journal
     *
     *
     * @return
     * @throws IOException
     */
    public boolean importSignals() throws IOException {

        Researcher user = this.securityService.getLoggedResearcher();
        String apiKey = user.getSignalsAPIKey();
        Logger.getGlobal().info(apiKey);

        Map URL_variables = Collections.emptyMap();
        //URL_variables.put()

        String getURL = this.baseURL;
        WebClient client = WebClient.create();

        ObjectNode responseSpec = client.get()
                .uri(this.baseURL + "/entities?page[offset]=0&page[limit]=1&includeTypes=journal")
                .header("x-api-key", apiKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                //.toEntity(String.class)
                .block();



        responseSpec.fields().forEachRemaining(System.out::println);

        return true;
    }

    /*public boolean importSignals() throws IOException {

        try {
            this.whenOpeningTrustStore_thenExceptionIsThrown();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Researcher user = this.securityService.getLoggedResearcher();
        String apiKey = user.getSignalsAPIKey();
        Logger.getGlobal().info(apiKey);

        int journalNumber = 0;
        URL myURL = new URL(this.baseURL + "/entities");  //"?page[offset]=" + journalNumber + "&page[limit]=1&includeTypes=journal&include=children%2C%20owner");

        HttpURLConnection con = (HttpURLConnection) myURL.openConnection();
        // If this header is present: 400 bad request
        // But if it is not present then: 415 unsupported media type
        con.setRequestProperty("Content-Type", "application/vnd.api+json");
        con.setRequestProperty("accept", "* /*");
        con.setRequestMethod("GET");
        con.setRequestProperty("x-api-key", apiKey);
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.close();


        StringBuilder sb = new StringBuilder();
        int HttpResult = con.getResponseCode();
        if (HttpResult == HttpURLConnection.HTTP_OK){
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));

            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();

            System.out.println(sb);
        }
        else
        {
            System.out.println(con.getResponseCode());
            System.out.println(con.getResponseMessage());
        }
        return true;
    }*/

    private KeyStore getKeyStore() throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {
        String filename = "/home/aleixmt/Desktop/eChempad/src/main/resources/CA_certificates/cacerts";
        FileInputStream is = new FileInputStream(filename);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(is, "changeit".toCharArray());
        return ks;
    }

    @Test
    public void whenOpeningTrustStore_thenExceptionIsThrown() throws Exception {
        KeyStore keyStore = getKeyStore();
        System.out.println(keyStore.size());
        System.out.println(keyStore.aliases().toString());
        Assertions.assertAll(() -> new PKIXParameters(keyStore));
    }

}
