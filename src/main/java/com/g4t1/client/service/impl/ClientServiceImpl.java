package com.g4t1.client.service.impl;

import java.lang.reflect.Field;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.g4t1.client.entity.Client;
import com.g4t1.client.exceptions.ClientNotFoundException;
import com.g4t1.client.exceptions.InvalidClientSourceDataException;
import com.g4t1.client.repository.ClientRepository;
import com.g4t1.client.service.ClientService;
import jakarta.transaction.Transactional;

@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clients;

    public ClientServiceImpl(ClientRepository clients) {
        this.clients = clients;
    }

    @Override
    public boolean validateSourceData(Client source, boolean create) {
        if (source == null) {
            return false;
        }

        try {
            for (Field field : Client.class.getDeclaredFields()) {
                field.setAccessible(true);
                Object fieldValue = field.get(source);
                String fieldName = field.getName();

                // id should always be null for both create and update operations
                if (fieldName.equals("id") && fieldValue != null) {
                    return false;
                }
                // in a create request: all non-id fields must be non-null
                if (create && !fieldName.equals("id") && fieldValue == null) {
                    return false;
                }
            }
            return true;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("reflection error during validation", e);
        }
    }

    @Override
    public boolean validateClient(String targetId) {
        if (!StringUtils.hasText(targetId)) {
            throw new IllegalArgumentException("client id must not be blank");
        }

        if (!clients.existsById(targetId)) {
            throw new ClientNotFoundException();
        }

        return true;
    }

    @Override
    public Client createClient(Client clientData) {

        // if client data is null or id field has a UUID, reject
        if (!validateSourceData(clientData, true)) {
            throw new InvalidClientSourceDataException();
        }

        try {
            String id = UUID.randomUUID().toString();
            clientData.setId(id);
            return clients.save(clientData);
        } catch (Exception e) { // catch any repo runtime error
            throw new RuntimeException("failed to create and save client", e);
        }
    }

    @Override
    @Transactional
    public Client updateClient(String id, Client source) {

        validateClient(id);
        if (!validateSourceData(source, false)) {
            throw new InvalidClientSourceDataException();
        }

        // Use pessimistic locking to avoid concurrent updates
        Client target = clients.findByIdWithLocking(id).orElseThrow(ClientNotFoundException::new);

        try {
            for (Field field : Client.class.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if ("id".equals(fieldName))
                    continue; // never update id

                Object value = field.get(source);
                if (value == null)
                    continue;

                // For Strings, only update if not blank
                if (value instanceof String && !StringUtils.hasText((String) value))
                    continue;
                field.set(target, value);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("failed to update client fields via reflection", e);
        }
        return clients.save(target);
    }

    @Override
    public Client getClient(String id) {
        validateClient(id);
        try {
            return clients.findById(id).get();
        } catch (Exception e) { // catch any repo runtime error
            throw new RuntimeException("failed to retrieve client", e);
        }
    }

    @Override
    public boolean deleteClient(String id) {
        validateClient(id);
        try {
            clients.deleteById(id);
            return true;
        } catch (Exception e) { // catch any repo runtime error
            throw new RuntimeException("failed to delete client", e);
        }
    }
}
