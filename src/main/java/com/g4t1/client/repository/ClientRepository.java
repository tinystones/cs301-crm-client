package com.g4t1.client.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.g4t1.client.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    List<Client> findAllByFirstName(String firstName);

    List<Client> findAllByLastName(String lastName);
}
