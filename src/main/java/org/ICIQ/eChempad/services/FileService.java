package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileService {
    /**
     * Stores the received file using the supplied UUID as filename.
     * @param file Chunks of raw data with some file metadata.
     * @param uuid Unique identifier of the associated document, which will be used as name of the file.
     * @return Name of the file as a String.
     */
    String storeFile(MultipartFile file, UUID uuid);

    /**
     * Stores the received file using the supplied UUID as filename.
     * @param file Chunks of raw data.
     * @param iEntity Entity object which will be used to obtain the unique identifier of the associated document, which
     *                will be used as name of the file.
     * @return Name of the file as a String.
     */
    String storeFile(MultipartFile file, JPAEntity iEntity);

    /**
     * Given a certain UUID returns the file that has the same UUID in the filename in the local filesystem.
     * @param uuid ID of the desired resource.
     * @return Chunk of raw data
     */
    Resource loadFileAsResource(UUID uuid);
}
