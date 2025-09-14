package com.g4t1.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.g4t1.client.entity.Client;
import com.g4t1.client.service.impl.ClientServiceImpl;


@RestController
public class ClientController {

    @Autowired
    private ClientServiceImpl clientService;

    @PostMapping("api/clients")
    public ResponseEntity<Client> create(@RequestBody Client client) {
        try {
            Client newlyCreated = clientService.createNewClient(client);
            return ResponseEntity.status(HttpStatus.CREATED).body(newlyCreated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("api/clients/{id}")
    public ResponseEntity<Client> getClient(@PathVariable String id) {
        try {
            Client client = clientService.getClientByUUID(id);
            if (client != null) {
                return ResponseEntity.status(HttpStatus.OK).body(client);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
