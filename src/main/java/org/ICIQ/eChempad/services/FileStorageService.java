/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.IEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileStorageService {

    /**
     * Stores the received file using the supplied uuid as filename
     * @param file Chunks of raw data
     * @param uuid Unique identifier of the associated document, which will be used as name of the file.
     * @return Name of the file as a String
     */
    String storeFile(MultipartFile file, UUID uuid);

    /**
     * Stores the received file using the supplied uuid as filename
     * @param file Chunks of raw data
     * @param iEntity Entity object which will be used to obtain the unique identifier of the associated document, which
     *                will be used as name of the file.
     * @return Name of the file as a String
     */
    String storeFile(MultipartFile file, IEntity iEntity);

    /**
     * Given a certain uuid returns the file that has the same uuid in the filename in the local filesystem
     * @param uuid ID of the desired resource.
     * @return Chunk of raw data
     */
    Resource loadFileAsResource(UUID uuid);
}
