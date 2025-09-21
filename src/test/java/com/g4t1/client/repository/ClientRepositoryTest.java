package com.g4t1.client.repository;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import com.g4t1.client.entity.Client;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    private Client testClient;

    @BeforeEach
    void setUp() {
        clientRepository.deleteAll();

        testClient = new Client();
        testClient.setId(UUID.randomUUID().toString());
        testClient.setFirstName("John");
        testClient.setLastName("Doe");
        testClient.setDateOfBirth(LocalDate.of(1990, 1, 15));
        testClient.setGender("Male");
        testClient.setEmailAddress("john.doe@example.com");
        testClient.setPhoneNumber("1234567890");
        testClient.setAddress("123 Main Street");
        testClient.setCity("New York");
        testClient.setState("New York");
        testClient.setCountry("United States");
        testClient.setPostalCode("10001");
        testClient.setValidated(false);
    }

    @Test
    @DisplayName("Should save and retrieve client")
    void saveAndRetrieve_Client_Success() {
        // When
        Client saved = clientRepository.save(testClient);
        Optional<Client> retrieved = clientRepository.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(testClient.getFirstName(), retrieved.get().getFirstName());
        assertEquals(testClient.getEmailAddress(), retrieved.get().getEmailAddress());
    }

    @Test
    @DisplayName("Should check client existence")
    void existsById_ExistingClient_ReturnsTrue() {
        // Given
        Client saved = clientRepository.save(testClient);

        // When
        boolean exists = clientRepository.existsById(saved.getId());

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should check non-existent client")
    void existsById_NonExistentClient_ReturnsFalse() {
        // Given
        String nonExistentId = UUID.randomUUID().toString();

        // When
        boolean exists = clientRepository.existsById(nonExistentId);

        // Then
        assertFalse(exists);
    }

    @Test
    @DisplayName("Should delete client")
    void deleteById_ExistingClient_DeletesSuccessfully() {
        // Given
        Client saved = clientRepository.save(testClient);
        assertTrue(clientRepository.existsById(saved.getId()));

        // When
        clientRepository.deleteById(saved.getId());

        // Then
        assertFalse(clientRepository.existsById(saved.getId()));
    }

    @Test
    @DisplayName("Should find client with locking")
    void findByIdWithLocking_ExistingClient_ReturnsClient() {
        // Given
        Client saved = clientRepository.save(testClient);

        // When
        Optional<Client> found = clientRepository.findByIdWithLocking(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(testClient.getFirstName(), found.get().getFirstName());
        assertEquals(testClient.getEmailAddress(), found.get().getEmailAddress());
    }

    @Test
    @DisplayName("Should return empty for non-existent client with locking")
    void findByIdWithLocking_NonExistentClient_ReturnsEmpty() {
        // Given
        String nonExistentId = UUID.randomUUID().toString();

        // When
        Optional<Client> found = clientRepository.findByIdWithLocking(nonExistentId);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should count all clients")
    void count_WithClients_ReturnsCorrectCount() {
        // Given
        assertEquals(0, clientRepository.count());

        clientRepository.save(testClient);

        Client anotherClient = new Client();
        anotherClient.setId(UUID.randomUUID().toString());
        anotherClient.setFirstName("Jane");
        anotherClient.setLastName("Smith");
        anotherClient.setDateOfBirth(LocalDate.of(1985, 6, 20));
        anotherClient.setGender("Female");
        anotherClient.setEmailAddress("jane.smith@example.com");
        anotherClient.setPhoneNumber("0987654321");
        anotherClient.setAddress("456 Oak Avenue");
        anotherClient.setCity("Los Angeles");
        anotherClient.setState("California");
        anotherClient.setCountry("United States");
        anotherClient.setPostalCode("90210");
        anotherClient.setValidated(true);

        clientRepository.save(anotherClient);

        // When
        long count = clientRepository.count();

        // Then
        assertEquals(2, count);
    }
}
