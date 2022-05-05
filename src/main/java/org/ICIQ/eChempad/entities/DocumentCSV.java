/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.entities;

/**
 * Extends the functionality of the abstract class Document for visualizing and manipulating CSV files
 */
public class DocumentCSV extends Document {

    /**
     * Constructor of DocumentCSV. It will perform a call to the constructor of the super class in order to initialize
     * its parameters.
     * @param name Name of the document (file)
     */
    public DocumentCSV(String name, String description, Experiment experiment, String contentType) {
        super(name, description, experiment, contentType);
    }




}
