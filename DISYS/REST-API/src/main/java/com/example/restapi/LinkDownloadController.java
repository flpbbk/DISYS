package com.example.restapi;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class LinkDownloadController {

    // Base directory for file storage
    private static final String FILE_STORAGE_BASE_PATH = "C:\\Users\\bibi\\Documents\\UNI\\DISYS\\Project";

    @GetMapping("/download/{customerID}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String customerID) {
        if (customerID == null || customerID.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null); // Invalid customerID
        }

        try {
            // Construct file path
            String fileName = "invoice_customerID_" + customerID + ".pdf";
            Path filePath = Paths.get(FILE_STORAGE_BASE_PATH, fileName).normalize();

            // Load file as Resource
            Resource resource = new UrlResource(filePath.toUri());

            // Check if the file exists and is readable
            if (resource.exists() && resource.isReadable()) {
                // Return file with headers
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                System.err.println("[REST API] File not found or not readable: " + fileName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (IOException ex) {
            System.err.println("[REST API] Error loading file: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
