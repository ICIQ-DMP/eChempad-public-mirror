/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2023 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * Import service specialized in importing data from Signals application.
 */
public interface SignalsImportService extends ImportService {

    /**
     * Parses the JSON received from a Signals entity to obtain the date of that entity.
     *
     * @param metadataJSON JSON of the entity.
     * @return Date object
     */
    public static Date parseDateFromJSON(ObjectNode metadataJSON)
    {
        String signalsJournalCreationDate = metadataJSON.get("data").get(0).get("attributes").get("createdAt").toString().replace("\"", "");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = dateFormat.parse(signalsJournalCreationDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}