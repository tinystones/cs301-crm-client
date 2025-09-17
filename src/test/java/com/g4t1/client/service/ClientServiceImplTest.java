package com.g4t1.client.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.g4t1.client.entity.Client;
import com.g4t1.client.exceptions.ClientNotFoundException;
import com.g4t1.client.exceptions.ExistingClientUUIDException;
import com.g4t1.client.exceptions.NullClientException;
import com.g4t1.client.repository.ClientRepository;
import com.g4t1.client.service.impl.ClientServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ClientServiceImplTest {

    @Mock
    private ClientRepository repository;

    @InjectMocks
    private ClientServiceImpl service;

    @Nested
    class CreateNewClientTests {
        @Test
        void createNewClient_givenNullClient_throwsNullClientException() {

            /* Arrange */
            Client input = null;
            NullClientException e;

            /* Act & Assert */
            e = assertThrows(NullClientException.class, () -> service.createNewClient(input));
            assertEquals(e.getMessage(), "client data must not be null");
        }

        @Test
        void createNewClient_givenClientWithUUID_throwsExistingClientUUIDException() {
            /* Arrange */
            Client input = new Client();
            input.setId("existing-id@2025");
            ExistingClientUUIDException e;

            /* Act & Assert */
            e = assertThrows(ExistingClientUUIDException.class,
                    () -> service.createNewClient(input));
            assertEquals(e.getMessage(), "client already has an id");
        }

        @Test
        void createNewClient_givenValidClient_savesSuccesfully() {
            /* Arrange */
            Client result;
            Client input = new Client();
            input.setId("");
            input.setFirstName("Apple");
            input.setLastName("Pie");
            ExistingClientUUIDException e;

            when(repository.save(any(Client.class))).thenReturn(input);

            /* Act & Assert */

            result = service.createNewClient(input);
            assertEquals(Client.class, result.getClass());
            assertEquals("Apple", result.getFirstName());
            assertEquals("Pie", result.getLastName());
            assertTrue(result.getId() instanceof String);
            System.out.println(result.getId());
        }
    }

    @Nested
    class getClientByUUIDTests {

        Client targetClient; // shared client for tests to use

        @BeforeEach
        void arrangeRepo() {
            Client input = new Client();
            input.setId("");
            input.setFirstName("Chicken");
            input.setLastName("Rice");

            when(repository.save(any(Client.class))).thenReturn(input);
            targetClient = service.createNewClient(input);
        }

        @Test
        void getClientByUUID_givenNullClientID_throwsRuntimeException() {
            /* Arrange */
            String nullId = null;

            /* Act & Assert */
            assertThrows(RuntimeException.class, () -> service.getClientByUUID(nullId));
        }

        @Test
        void getClientByUUID_givenMissingClient_throwsClientNotFoundException() {
            /* Arrange */
            String missingId = "nonexistent-id";

            /* Act & Assert */
            assertThrows(ClientNotFoundException.class, () -> service.getClientByUUID(missingId));
        }

        @Test
        void getClientByUUID_givenPresentClient_returnsClient() {
            /* Arrange */
            String targetId = targetClient.getId();

            when(repository.findById(targetId)).thenReturn(Optional.of(targetClient));

            /* Act */
            Client result = service.getClientByUUID(targetId);

            /* Assert */
            assertNotNull(result); // check if client is found
            assertEquals(targetId, result.getId()); // check if same UUID
            assertEquals("Chicken", result.getFirstName());
            assertEquals("Rice", result.getLastName());
        }
    }

    @Nested
    class updateClientInfoTests {

        Client targetClient;
        Client sourceClient;

        @BeforeEach
        void arrangeRepo() {
            Client targetDummy = new Client();
            targetDummy.setId("");
            targetDummy.setFirstName("Crab");
            targetDummy.setLastName("Wiggle");
            targetDummy.setCountry("sInGapoUR");

            when(repository.save(any(Client.class))).thenReturn(targetDummy);
            targetClient = service.createNewClient(targetDummy);

            Client sourceDummy = new Client();
            sourceDummy.setId("");
            sourceDummy.setFirstName("Fuzzy");
            sourceDummy.setCountry("Singapore");

            when(repository.save(any(Client.class))).thenReturn(sourceDummy);
            sourceClient = service.createNewClient(sourceDummy);
        }

        @Test
        void updateClientInfo_givenNullClientID_RuntimeException() {
            /* Arrange */
            String nullId = null;

            /* Act & Assert */
            assertThrows(RuntimeException.class,
                    () -> service.updateClientInfo(nullId, sourceClient));
        }

        @Test
        void updateClientInfo_givenMissingClientID_throwsClientNotFoundException() {
            /* Arrange */
            String missingId = "nonexistent-id";
            when(repository.findByIdWithLocking(missingId)).thenReturn(Optional.empty());

            /* Act & Assert */
            assertThrows(ClientNotFoundException.class,
                    () -> service.updateClientInfo(missingId, sourceClient));
        }


        @Test
        void updateClientInfo_givenNullSource_throwsNullClientException() {
            /* Arrange */
            String targetId = targetClient.getId();

            /* Act & Assert */
            assertThrows(NullClientException.class, () -> service.updateClientInfo(targetId, null));
        }

        @Test
        void updateClientInfo_givenValidSource_updatetargetClientSuccessfully() {
            /* Arrange */
            String targetId = targetClient.getId();
            Client result;

            // Mock repository to return the target client when locking is used
            when(repository.findByIdWithLocking(targetId)).thenReturn(Optional.of(targetClient));
            // Mock repository to return the updated client after save
            when(repository.save(any(Client.class))).thenReturn(targetClient);

            /* Act & Assert */
            result = service.updateClientInfo(targetId, sourceClient);
            assertNotNull(result);
            assertEquals("Fuzzy", result.getFirstName());
            assertEquals("Wiggle", result.getLastName());
            assertEquals("Singapore", result.getCountry());
            assertNull(result.getDateOfBirth());
            assertNull(result.getGender());
            assertNull(result.getAddress());
        }
    }

    @Nested
    class deleteClientTests {

        Client targetClient;

        @BeforeEach
        void arrangeRepo() {
            Client input = new Client();
            input.setId("");
            input.setFirstName("Hydro");
            input.setLastName("Flask");

            when(repository.save(any(Client.class))).thenReturn(input);
            targetClient = service.createNewClient(input);
        }

        @Test
        void deleteClient_givenNullClientID_throwsRuntimeException() {
            /* Arrange */
            String nullID = null;

            /* Act & Assert */
            assertThrows(RuntimeException.class, () -> service.deleteClient(nullID));
        }

        @Test
        void deleteClient_givenMissingClient_throwsClientNotFoundException() {
            /* Arrange */
            String missingId = "nonexistent-id";

            /* Act & Assert */
            assertThrows(ClientNotFoundException.class, () -> service.deleteClient(missingId));
        }

        @Test
        void deleteClient_givenPresentClient_deleteSuccesfully() {
            /* Arrange */
            String targetId = targetClient.getId();
            boolean result = false;

            when(repository.existsById(targetId)).thenReturn(true);

            /* Act & Assert */
            result = service.deleteClient(targetId);
            assertTrue(result);
            assertThrows(ClientNotFoundException.class, () -> service.getClientByUUID(targetId));
        }
    }
}
