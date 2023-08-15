package com.iqbaal.triptour.service.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import org.springframework.web.multipart.MultipartFile;

import com.iqbaal.triptour.exception.ResourceNotFoundException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

@Component
public class UploadFile {
    public UploadFile(){
    }

    private final Path root = Paths.get("uploads");

    public void initUpload() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public String loadPathFile(String filename) {
        if(filename == "" || filename == null){
            return "";
        }
        try {
            Path file = root.resolve(filename);
            return  file.toAbsolutePath().toString();
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public void saveFile(MultipartFile file){
        try {
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new RuntimeException("A file of that name already exists.");
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean deleteFile(String filename) {
        try {
            Path file = root.resolve(filename);
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public Resource loadFileAsResource(String fileName) throws ResourceNotFoundException {
        try {
            Path filePath = root.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Resource file is not found: "+ fileName);
            }
        } catch (MalformedURLException exception) {
            throw new ResourceNotFoundException("Resource file is not found: " + fileName, exception);
        }
    }

}
