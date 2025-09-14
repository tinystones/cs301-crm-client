package com.g4t1.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        Client newlyCreated = clientService.createNewClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(newlyCreated);
    }



}
