package com.g4t1.client.service;

import com.g4t1.client.entity.Client;

public interface ClientService {
    Client createClient(Client clientData);

    Client updateClient(String id, Client source);

    Client getClient(String id);

    boolean deleteClient(String id);

    boolean validateSourceData(Client source, boolean create);

    boolean validateClient(String targetId);
}
