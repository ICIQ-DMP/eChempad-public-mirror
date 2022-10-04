package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.configurations.wrappers.FileStorageProperties;
import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntity;
import org.ICIQ.eChempad.exceptions.FileStorageException;
import org.ICIQ.eChempad.exceptions.MyFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{

    private final Path fileStorageLocation;

    @Autowired
    public FileServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory " + this.fileStorageLocation + " where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, JPAEntity jpaEntity) {

        return this.storeFile(file, (UUID) jpaEntity.getId());
    }

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

            return targetLocation.toString();
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

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
