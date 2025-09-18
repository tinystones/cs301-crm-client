package com.g4t1.client.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.g4t1.client.entity.Client;
import com.g4t1.client.exceptions.ClientNotFoundException;
import com.g4t1.client.exceptions.InvalidClientSourceDataException;
import com.g4t1.client.repository.ClientRepository;
import com.g4t1.client.service.impl.ClientServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ClientServiceImplTest {

    @Mock
    private ClientRepository repository;

    @InjectMocks
    private ClientServiceImpl service;

    private Client arrangeGoodSource() {
        Client goodSource = new Client(null, "Hop", "Pod", LocalDate.now(), "Non-Binary",
                "hippityhoppity@hoparound.com", "0908 1965", "71 hopping garden", "Singapore",
                "Singapore", "Singapore", "317109", false);
        return goodSource;
    }

    private Client arrangeBadSource() {
        Client goodSource = new Client(null, "Hop", "Pod", LocalDate.now(), null, null, "0908 1965",
                "71 hopping garden", null, "Singapore", null, "317109", false);
        return goodSource;
    }

    private Client arrangeUpdateSource() {
        Client goodSource = new Client(null, null, "Scotch", null, null, null, null,
                "101 kinder grounds", "Zurich", "Zurich", "Switzerland", "8111013", true);
        return goodSource;
    }

    // TODO: write tests for ValidateClient()

    @Nested
    class ValidateSourceDataTests {

        /* Arrange */
        Client goodSource;
        Client badSource;

        @BeforeEach
        void arrangeSources() {
            goodSource = arrangeGoodSource();
            badSource = arrangeBadSource();
        }

        @Test
        void validateSourceData_givenGoodSourceForCreate_returnTrue() {
            /* Act & Assert */
            boolean result = service.validateSourceData(goodSource, true);
            assertTrue(result);
        }

        @Test
        void validateSourceData_givenBadSourceForCreate_returnFalse() {
            /* Act & Assert */
            boolean result = service.validateSourceData(badSource, true);
            assertFalse(result);
        }

        @Test
        void validateSourceData_givenAnySourceForUpdate1_returnTrue() {
            /* Act & Assert */
            boolean result = service.validateSourceData(goodSource, false);
            assertTrue(result);
        }

        @Test
        void validateSourceData_givenAnySourceForUpdate2_returnTrue() {
            /* Act & Assert */
            boolean result = service.validateSourceData(badSource, false);
            assertTrue(result);
        }

        @Test
        void validateSourceData_givenNull_returnFalse() {
            /* Act & Assert */
            boolean result = service.validateSourceData(null, false);
            assertFalse(result);
        }

        @Test
        void validateSourceData_givenSourceWithID_returnFalse() {
            /* Act & Assert */
            goodSource.setId("existing-id");
            boolean result = service.validateSourceData(goodSource, false);
            assertFalse(result);
        }
    }

    @Nested
    class CreateClientTests {

        /* Arrange */
        Client goodSource;
        Client badSource;
        InvalidClientSourceDataException ex;

        @BeforeEach
        void arrangeSources() {
            goodSource = arrangeGoodSource();
            badSource = arrangeBadSource();
            ex = null;
        }

        @Test
        void createClient_givenNull_throwsInvalidClientSourceDataException() {
            /* Act & Assert */
            ex = assertThrows(InvalidClientSourceDataException.class,
                    () -> service.createClient(null));
            assertEquals(ex.getMessage(), "invalid client source data, please check fields");
        }

        @Test
        void createClient_givenClientWithID_throwsInvalidClientSourceDataException() {
            /* Arrange */
            goodSource.setId("existing-id");
            /* Act & Assert */
            ex = assertThrows(InvalidClientSourceDataException.class,
                    () -> service.createClient(goodSource));
            assertEquals(ex.getMessage(), "invalid client source data, please check fields");
        }

        @Test
        void createClient_givenBadSource_throwsInvalidClientSourceDataException() {
            /* Act & Assert */
            ex = assertThrows(InvalidClientSourceDataException.class,
                    () -> service.createClient(badSource));
            assertEquals(ex.getMessage(), "invalid client source data, please check fields");
        }

        @Test
        void createClient_givenGoodSource_savesSuccessfully() {
            /* Arrange */
            when(repository.save(any(Client.class))).thenReturn(goodSource);
            /* Act & Assert */
            Client result = service.createClient(goodSource);
            assertEquals(Client.class, result.getClass());
            assertEquals("Hop", result.getFirstName());
            assertEquals("Pod", result.getLastName());
            assertEquals("Non-Binary", result.getGender());
            assertEquals("317109", result.getPostalCode());
            assertTrue(result.getId() instanceof String);
        }
    }

    @Nested
    class UpdateClientTests {

        /* Arrage */
        Client targetClient;
        Client sourceClient;
        String targetId;

        @BeforeEach
        void arrangeRepo() {
            targetClient = arrangeGoodSource();
            targetClient.setId("target-id-123");
            sourceClient = arrangeUpdateSource();
            targetId = targetClient.getId();
        }

        @Test
        void updateClient_givenNull_throwsIllegalArgumentException() {
            /* Act & Assert */
            assertThrows(IllegalArgumentException.class,
                    () -> service.updateClient(null, sourceClient));
            assertThrows(IllegalArgumentException.class,
                    () -> service.updateClient("   ", sourceClient));
        }

        @Test
        void updateClient_givenNullSource_throwsInvalidClientSourceDataException() {
            /* Arrage */
            when(repository.existsById(targetId)).thenReturn(true);
            when(repository.findByIdWithLocking(targetId))
                    .thenReturn(java.util.Optional.of(targetClient));
            /* Act & Assert */
            assertThrows(InvalidClientSourceDataException.class,
                    () -> service.updateClient(targetId, null));
        }

        @Test
        void updateClient_givenRandomTargetId_throwsClientNotFoundException() {
            /* Arrage */
            String missingId = "1-very-challenging-brick-wall";
            when(repository.existsById(missingId)).thenReturn(true);
            when(repository.findByIdWithLocking(missingId)).thenReturn(java.util.Optional.empty());

            /* Act & Assert */
            assertThrows(ClientNotFoundException.class,
                    () -> service.updateClient(missingId, sourceClient));
        }

        @Test
        void updateClient_givenValidSource_updatesSuccessfully() {

            /* Arrage */
            when(repository.existsById(targetId)).thenReturn(true);
            when(repository.findByIdWithLocking(targetId))
                    .thenReturn(java.util.Optional.of(targetClient));
            when(repository.save(any(Client.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            /* Act & Assert */
            Client result = service.updateClient(targetId, sourceClient);
            // Only non-null/non-blank fields from sourceClient should be updated
            assertNotNull(result);
            assertEquals(targetId, result.getId());
            assertEquals("Hop", result.getFirstName()); // not updated (source null)
            assertEquals("Scotch", result.getLastName()); // updated
            assertEquals("101 kinder grounds", result.getAddress()); // updated
            assertEquals("Zurich", result.getCity()); // updated
            assertEquals("Zurich", result.getState()); // updated
            assertEquals("Switzerland", result.getCountry()); // updated
            assertEquals("8111013", result.getPostalCode()); // updated
            assertTrue(result.isValidated()); // updated
        }
    }

    @Nested
    class GetClientTests {

        /* Arrange */
        Client targetClient;
        String targetId;
        RuntimeException ex;

        @BeforeEach
        void arrangeRepo() {
            Client goodSource = arrangeGoodSource();
            ex = null;

            when(repository.save(any(Client.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            targetClient = service.createClient(goodSource);
            targetId = targetClient.getId();
        }

        @Test
        void getClient_givenNull_throwIllegalArgumentException() {
            /* Act & Assert */
            ex = assertThrows(IllegalArgumentException.class, () -> service.getClient(null));
            assertEquals(ex.getMessage(), "client id must not be blank");
        }

        @Test
        void getClient_givenBlankId_throwIllegalArgumentException() {
            /* Act & Assert */
            ex = assertThrows(IllegalArgumentException.class, () -> service.getClient("   "));
            assertEquals(ex.getMessage(), "client id must not be blank");
        }

        @Test
        void getClient_givenRandomTargetId_throwClientNotFoundException() {
            /* Act & Assert */
            ex = assertThrows(ClientNotFoundException.class,
                    () -> service.getClient("1-very-challenging-brick-wall"));
            assertEquals(ex.getMessage(), "client not found");
        }

        @Test
        void getClient_givenExisitingTargetId_returnsClient() {

            /* Arrage */
            when(repository.existsById(targetId)).thenReturn(true);
            when(repository.findById(targetId)).thenReturn(java.util.Optional.of(targetClient));

            /* Act & Assert */
            Client result = service.getClient(targetId);
            assertNotNull(result); // check if client is found
            assertEquals(targetId, result.getId()); // check if same UUID
            assertEquals(Client.class, result.getClass());
            assertEquals("Hop", result.getFirstName());
            assertEquals("Pod", result.getLastName());
            assertEquals("Non-Binary", result.getGender());
            assertEquals("317109", result.getPostalCode());
        }
    }

    @Nested
    class DeleteClientTests {

        /* Arrange */
        Client targetClient;
        String targetId;
        RuntimeException ex;

        @BeforeEach
        void arrangeRepo() {
            Client goodSource = arrangeGoodSource();
            ex = null;

            when(repository.save(any(Client.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            targetClient = service.createClient(goodSource);
            targetId = targetClient.getId();
        }

        @Test
        void deleteClient_givenNull_throwIllegalArgumentException() {
            /* Act & Assert */
            ex = assertThrows(IllegalArgumentException.class, () -> service.deleteClient(null));
            assertEquals(ex.getMessage(), "client id must not be blank");
        }

        @Test
        void deleteClient_givenBlank_throwIllegalArgumentException() {
            /* Act & Assert */
            ex = assertThrows(IllegalArgumentException.class, () -> service.deleteClient("   "));
            assertEquals(ex.getMessage(), "client id must not be blank");
        }

        @Test
        void deleteClient_givenRandomTargetId_throwClientNotFoundException() {
            /* Act & Assert */
            ex = assertThrows(ClientNotFoundException.class,
                    () -> service.deleteClient("1-very-challenging-brick-wall"));
            assertEquals(ex.getMessage(), "client not found");
        }

        @Test
        void deleteClient_givenRandomTargetId_returnTrue() {
            /* Arrage */
            when(repository.existsById(targetId)).thenReturn(true);

            /* Act & Assert */
            assertTrue(service.deleteClient(targetId));
        }
    }
}
