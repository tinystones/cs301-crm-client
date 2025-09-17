package com.g4t1.client.service.impl;

import java.lang.reflect.Field;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.g4t1.client.entity.Client;
import com.g4t1.client.exceptions.ClientNotFoundException;
import com.g4t1.client.exceptions.ExistingClientUUIDException;
import com.g4t1.client.exceptions.NullClientException;
import com.g4t1.client.repository.ClientRepository;
import jakarta.transaction.Transactional;

@Service
public class ClientServiceImpl {

    // boolean deleteClient(String id);

    @Autowired
    private ClientRepository clients;

    public Client createNewClient(Client clientData) {
        if (clientData == null) { // check if client data is valid
            throw new NullClientException();
        }

        if (!clientData.getId().isBlank()) { // check if client has existing id
            throw new ExistingClientUUIDException();
        }

        try {
            String id = UUID.randomUUID().toString(); // generate UUID
            clientData.setId(id); // assign UUID
            return clients.save(clientData);
        } catch (Exception e) {
            throw new RuntimeException("failed to create and save client", e);
        }
    }

    public Client getClientByUUID(String id) {
        try {
            return clients.findById(id).orElseThrow(ClientNotFoundException::new);
        } catch (ClientNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("failed to retrieve client", e);
        }
    }

    @Transactional
    public Client updateClientInfo(Client target, Client source) {
        if (target == null || source == null) { // check if inputs are valid
            throw new NullClientException();
        }

        Field[] fields = Client.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true); // allow access to private fields
            try {
                Object value = field.get(source);
                if (value != null) {
                    field.set(target, value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to update field: " + field.getName(), e);
            }
        }
        return clients.save(target);
    }

    @Transactional
    public Client updateClientInfoById(String id, Client source) {
        Client target = clients.findByIdWithLocking(id).orElseThrow(ClientNotFoundException::new);

        Field[] fields = Client.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(source);
                if (value != null) {
                    field.set(target, value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to update field: " + field.getName(), e);
            }
        }
        return clients.save(target);
    }

    public boolean deleteClient(String id) {
        try {
            if (!clients.existsById(id)) {
                throw new ClientNotFoundException();
            }
            clients.deleteById(id);
            return true;
        } catch (ClientNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("failed to delete client", e);
        }
    }
}
