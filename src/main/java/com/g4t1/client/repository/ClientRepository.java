package com.g4t1.client.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.g4t1.client.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    List<Client> findByFirstName(String firstName);

    List<Client> findByLastName(String lastName);

    List<Client> findByFirstNameAndLastName(String firstName, String lastName);
}
