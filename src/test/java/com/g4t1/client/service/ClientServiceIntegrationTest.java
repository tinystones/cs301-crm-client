package com.g4t1.client.service;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import com.g4t1.client.config.TestSecurityConfig;
import com.g4t1.client.entity.Client;
import com.g4t1.client.repository.ClientRepository;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(TestSecurityConfig.class)
public class ClientServiceIntegrationTest {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientRepository clientRepository;

    private Client validClient;

    @BeforeEach
    void setUp() {
        clientRepository.deleteAll();

        validClient = new Client(null, "John", "Doe", LocalDate.of(1990, 1, 15), "Male",
                "john.doe@example.com", "1234567890", "123 Main Street", "New York", "New York",
                "United States", "10001", false);
    }

    @Nested
    @DisplayName("Create Client Service Tests")
    class CreateClientServiceTests {

        @Test
        @DisplayName("Should create client successfully with valid data")
        void createClient_ValidData_ReturnsClient() {
            // When
            Client created = clientService.createClient(validClient);

            // Then
            assertNotNull(created);
            assertNotNull(created.getId());
            assertEquals("John", created.getFirstName());
            assertEquals("Doe", created.getLastName());
            assertEquals("john.doe@example.com", created.getEmailAddress());
            assertTrue(clientRepository.existsById(created.getId()));
        }
    }

    @Test
    @DisplayName("Should handle concurrent updates with pessimistic locking")
    void updateClient_ConcurrentUpdates_HandlesCorrectly()
            throws InterruptedException, ExecutionException, TimeoutException {
        // Given
        Client saved = clientService.createClient(validClient);
        String clientId = saved.getId();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            // When - Submit concurrent updates
            Future<Client> future1 = executor.submit(() -> {
                Client update = new Client(null, "FirstUpdate", null, null, null, null, null, null,
                        null, null, null, null, false);
                return clientService.updateClient(clientId, update);
            });

            Future<Client> future2 = executor.submit(() -> {
                Client update = new Client(null, "SecondUpdate", null, null, null, null, null, null,
                        null, null, null, null, false);
                return clientService.updateClient(clientId, update);
            });

            // Then - Both operations should complete successfully
            Client result1 = future1.get(5, TimeUnit.SECONDS);
            Client result2 = future2.get(5, TimeUnit.SECONDS);

            assertNotNull(result1);
            assertNotNull(result2);

            // Verify no data corruption occurred
            Client finalState = clientService.getClient(clientId);
            assertTrue(
                    finalState.getFirstName().equals("FirstUpdate")
                            || finalState.getFirstName().equals("SecondUpdate"),
                    "Final state should be one of the updates, not corrupted data");

        } finally {
            executor.shutdown();
        }
    }

    @Nested
    @DisplayName("Get Client Service Tests")
    class GetClientServiceTests {

        @Test
        @DisplayName("Should retrieve existing client")
        void getClient_ExistingId_ReturnsClient() {
            // Given
            Client saved = clientService.createClient(validClient);

            // When
            Client retrieved = clientService.getClient(saved.getId());

            // Then
            assertNotNull(retrieved);
            assertEquals(saved.getId(), retrieved.getId());
            assertEquals(saved.getFirstName(), retrieved.getFirstName());
            assertEquals(saved.getEmailAddress(), retrieved.getEmailAddress());
        }
    }


    @Nested
    @DisplayName("Delete Client Service Tests")
    class DeleteClientServiceTests {

        @Test
        @DisplayName("Should delete existing client")
        void deleteClient_ExistingId_DeletesSuccessfully() {
            // Given
            Client saved = clientService.createClient(validClient);
            String clientId = saved.getId();
            assertTrue(clientRepository.existsById(clientId));

            // When
            boolean result = clientService.deleteClient(clientId);

            // Then
            assertTrue(result);
            assertFalse(clientRepository.existsById(clientId));
        }
    }
}
