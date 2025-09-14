package com.g4t1.client.service;

import java.util.Optional;
import com.g4t1.client.entity.Client;

public interface ClientService {
    Client createNewClient(Client newClient);

    Optional<Client> getClientByUUID(String id);

    Client updateClientDetails();

    boolean deleteClient(String id);

}
