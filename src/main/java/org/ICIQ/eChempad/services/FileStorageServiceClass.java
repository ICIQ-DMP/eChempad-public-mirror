/**
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.configurations.FileStorageProperties;
import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.exceptions.FileStorageException;
import org.ICIQ.eChempad.exceptions.MyFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceClass implements FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageServiceClass(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }


    /**
     * Stores the received file using the supplied uuid as filename
     * @param file Chunks of raw data
     * @param iEntity Entity object which will be used to obtain the unique identifier of the associated document, which
     *                will be used as name of the file.
     * @return Name of the file as a String
     */
    @Override
    public String storeFile(MultipartFile file, IEntity iEntity) {

        return this.storeFile(file, iEntity.getUUid());
    }


    /**
     * Stores the received file using the supplied uuid as filename
     * @param file Chunks of raw data
     * @param uuid Unique identifier of the associated document, which will be used as name of the file.
     * @return Name of the file as a String
     */
    @Override
    public String storeFile(MultipartFile file, UUID uuid) {
        // Normalize file name
        String fileName = uuid.toString();

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    /**
     * Given a certain uuid returns the file that has the same uuid in the filename in the local filesystem
     * @param uuid ID of the desired resource.
     * @return Chunk of raw data
     */
    @Override
    public Resource loadFileAsResource(UUID uuid) {
        try
        {
            Path filePath = this.fileStorageLocation.resolve(uuid.toString()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + filePath);
            }
        }
        catch (MalformedURLException ex)
        {
            throw new MyFileNotFoundException("File not found " + uuid, ex);
        }
    }
}

