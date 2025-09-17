package com.g4t1.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
        Client newlyCreated = clientService.createNewClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(newlyCreated);
    }

    // verify endpoint

    @PutMapping("api/clients/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable String id,
            @RequestBody Client source) {

        Client client = clientService.updateClientInfo(id, source);
        return ResponseEntity.status(HttpStatus.OK).body(client);
    }

    @GetMapping("api/clients/{id}")
    public ResponseEntity<Client> getClient(@PathVariable String id) {
        Client client = clientService.getClientByUUID(id);
        return ResponseEntity.status(HttpStatus.OK).body(client);
    }

    @DeleteMapping("api/clients/{id}")
    public ResponseEntity<Client> deleteClient(@PathVariable String id) {
        clientService.deleteClient(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
