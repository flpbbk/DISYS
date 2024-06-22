package com.example.restapi;

import com.example.restapi.queue.RabbitMQ;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final RabbitMQ rabbitMQ = new RabbitMQ();

    private static final String FILE_STORAGE_BASE_PATH = "C:\\Users\\vfich\\IdeaProjects\\DISYS\\FileStorage";

    @PostMapping("/{id}")
    public ResponseEntity<String> startDataGathering(@PathVariable int id) {
        try {
            // Delete any existing file for the given customer ID
            deleteFile(id);
            // Send a message to RabbitMQ to start the data gathering job
            rabbitMQ.send("red", String.valueOf(id));
            return ResponseEntity.ok("Data gathering job started for customer ID: " + id);
        } catch (Exception e) {
            System.err.println("[REST API] Error starting data gathering: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to start data gathering job.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getInvoice(@PathVariable int id) {
        if (isFileExisting(id)) {
            // Sends HTTP status code 200 and the download link for the PDF
            String downloadLink = "http://localhost:8082/download/" + id;
            return ResponseEntity.ok(downloadLink);
        } else {
            // Sends HTTP status code 404 if no file is found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice not found for customer ID: " + id);
        }
    }

    private boolean isFileExisting(int customerID) {
        // Constructs the file path
        Path filePath = Paths.get(FILE_STORAGE_BASE_PATH, "invoice_customerID_" + customerID + ".pdf").normalize();
        // Checks if the file exists and is not a directory
        return Files.exists(filePath) && !Files.isDirectory(filePath);
    }

    private void deleteFile(int customerID) throws IOException {
        // Constructs the file path
        Path filePath = Paths.get(FILE_STORAGE_BASE_PATH, "invoice_customerID_" + customerID + ".pdf").normalize();
        // Deletes the file if it exi
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            System.out.println("[REST API] Deleted file: " + filePath);
        } else {
            System.out.println("[REST API] No file to delete for customer ID: " + customerID);
        }
    }
}
