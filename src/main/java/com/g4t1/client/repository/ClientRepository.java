package com.g4t1.client.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import com.g4t1.client.entity.Client;
import jakarta.persistence.LockModeType;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    List<Client> findByFirstName(String firstName);

    List<Client> findByLastName(String lastName);

    List<Client> findByFirstNameAndLastName(String firstName, String lastName);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("SELECT c FROM Client c WHERE c.id = :id")
    Optional<Client> findByIdWithLocking(String id);

}
