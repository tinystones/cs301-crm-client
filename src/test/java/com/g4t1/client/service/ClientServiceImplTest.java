package com.g4t1.client.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.g4t1.client.entity.Client;
import com.g4t1.client.repository.ClientRepository;
import com.g4t1.client.service.impl.ClientServiceImpl;

public class ClientServiceImplTest {

    @Mock
    private ClientRepository clients;

    @InjectMocks
    private ClientServiceImpl service;

    @Test
    void createNewClient_assignsUniqueUUID_saveSuccessful() {
        // Arrange
        Client input = new Client();
        input.setId(""); // avoid NPE from isBlank()

        when(clients.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Client created = service.createNewClient(input);

        // Assert
        assertNotNull(created, "created client should not be null");
        assertNotNull(created.getId(), "id should not be null");
        assertFalse(created.getId().isBlank(), "id should not be blank");
        verify(clients, times(1)).save(created);
    }

    @Test
    void createNewClient_nullClient_throwsRuntimeException() {
        // Act & Assert
        RuntimeException ex =
                assertThrows(RuntimeException.class, () -> service.createNewClient(null));
        assertNotNull(ex.getCause(), "cause should be present");
        assertTrue(ex.getCause() instanceof IllegalArgumentException,
                "cause should be IllegalArgumentException");
        assertTrue(ex.getCause().getMessage().contains("client data must not be null"));
        verify(clients, never()).save(any());
    }

    @Test
    void createNewClient_existingId_throwsRuntimeException() {
        // Arrange
        Client input = new Client();
        input.setId("existing-id123"); // non-blank id should trigger check

        // Act & Assert
        RuntimeException ex =
                assertThrows(RuntimeException.class, () -> service.createNewClient(input));
        assertNotNull(ex.getCause(), "cause should be present");
        assertTrue(ex.getCause() instanceof IllegalArgumentException,
                "cause should be IllegalArgumentException");
        assertTrue(ex.getCause().getMessage().contains("Client already has an ID"));
        verify(clients, never()).save(any());
    }
}
