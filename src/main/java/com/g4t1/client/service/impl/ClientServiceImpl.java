package com.g4t1.client.service.impl;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.g4t1.client.entity.Client;
import com.g4t1.client.repository.ClientRepository;

@Service
public class ClientServiceImpl {

    // Client createNewClient();
    // Optional<Client> getClientByUUID(String id);
    // Client updateClientDetails();
    // boolean deleteClient(String id);

    @Autowired
    private ClientRepository clients;


    public Client createNewClient(Client clientData) {
        try {
            if (clientData == null) { // check if client data is valid
                throw new IllegalArgumentException("client data must not be null");
            }

            if (!clientData.getId().isBlank()) { // check if client has existing id
                throw new IllegalArgumentException("Client already has an ID");
            }

            String id = UUID.randomUUID().toString(); // generate UUID
            clientData.setId(id); // assign UUID
            return clients.save(clientData);
        } catch (Exception e) {
            throw new RuntimeException("failed to create client", e);
        }
    }
}
