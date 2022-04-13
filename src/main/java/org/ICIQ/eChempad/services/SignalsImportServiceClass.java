package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Researcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
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


    public boolean importSignals()
    {
        Researcher user = this.securityService.getLoggedResearcher();
        String apiKey = user.getSignalsAPIKey();
        Logger.getGlobal().info(apiKey);

        URL myURL = null;
        int journalNumber = 0;
        try {
            myURL = new URL(this.baseURL + "/entities");
                    //"?page[offset]=" + journalNumber + "&page[limit]=1&includeTypes=journal&include=children%2C%20owner");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) myURL.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // If this header is present: 400 bad request
        // But if it is not present then: 415 unsupported media type
        con.setRequestProperty("Content-Type", "application/vnd.api+json");
        con.setRequestProperty("Accept", "*/*");
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        con.setRequestProperty("x-api-key", apiKey);
        con.setDoOutput(true);
        OutputStream os = null;
        try {
            os = con.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        StringBuilder sb = new StringBuilder();
        int HttpResult = 0;
        try {
            HttpResult = con.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }




        if (HttpResult == HttpURLConnection.HTTP_OK){
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }

            String line = null;
            while (true) {
                try {
                    if ((line = br.readLine()) == null) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sb.append(line).append("\n");
            }
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(sb);

        }
        else
        {
            try {
                System.out.println(con.getResponseCode());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                System.out.println(con.getResponseMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

}
