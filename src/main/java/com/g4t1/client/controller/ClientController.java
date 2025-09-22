package com.g4t1.client.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.g4t1.client.entity.Client;
import com.g4t1.client.service.ClientService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@Validated
public class ClientController {
    
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/api/clients")
    public ResponseEntity<Client> create(@Valid @RequestBody Client client) {
        Client newlyCreated = clientService.createClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(newlyCreated);
    }

    @PutMapping("/api/clients/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable @NotBlank String id,
            @Valid @RequestBody Client source) {
        Client client = clientService.updateClient(id, source);
        return ResponseEntity.status(HttpStatus.OK).body(client);
    }

    @GetMapping("/api/clients/{id}")
    public ResponseEntity<Client> getClient(@PathVariable @NotBlank String id) {
        Client client = clientService.getClient(id);
        return ResponseEntity.status(HttpStatus.OK).body(client);
    }

    @DeleteMapping("/api/clients/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable @NotBlank String id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();

        try {
            // Check if service and database are healthy
            boolean isHealthy = clientService.healthCheck();

            if (isHealthy) {
                status.put("status", "UP");
                status.put("database", "UP");
                status.put("service", "client-service");
                status.put("timestamp", java.time.Instant.now());
                return ResponseEntity.ok(status);
            } else {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            status.put("status", "DOWN");
            status.put("error", "Health check failed");
            status.put("service", "client-service");
            status.put("timestamp", java.time.Instant.now());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(status);
        }
    }
}
