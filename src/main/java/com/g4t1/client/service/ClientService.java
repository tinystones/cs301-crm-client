package com.g4t1.client.service;

import com.g4t1.client.entity.Client;

public interface ClientService {
    Client createNewClient(Client newClient);

    Client getClientByUUID(String id);

    Client updateClientDetails();

    boolean deleteClient(String id);

}
