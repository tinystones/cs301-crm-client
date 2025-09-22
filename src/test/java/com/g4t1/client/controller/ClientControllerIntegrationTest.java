package com.g4t1.client.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.g4t1.client.config.TestSecurityConfig;
import com.g4t1.client.entity.Client;
import com.g4t1.client.repository.ClientRepository;
import com.g4t1.client.service.ClientService;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(TestSecurityConfig.class)
public class ClientControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientService clientService;

    private Client validClient;
    private Client invalidClient;
    private Client updateClient;

    @BeforeEach
    void setUp() {
        clientRepository.deleteAll();

        validClient = new Client(null, "Hop", "Pod", LocalDate.of(1990, 5, 15), "Non-Binary",
                "hippityhoppity@hoparound.com", "09081965", "71 hopping garden street", "Singapore",
                "Singapore", "Singapore", "317109", false);

        // Invalid client: missing required fields and invalid data
        invalidClient = new Client(null, "H", "Pod", LocalDate.now().plusDays(1), null,
                "invalid-email", "0908 1965", "71 hopping garden street", null, "Singapore", null,
                "317109", false);

        updateClient = new Client(null, "UpdatedName", "Scotch", LocalDate.of(1985, 3, 10), "Male",
                "scotch@example.com", "41123456789", "101 kinder grounds avenue", "Zurich",
                "Zurich", "Switzerland", "8111013", true);
    }

    @Nested
    @DisplayName("Create Client Tests")
    class CreateClientTests {

        @Test
        @DisplayName("Should return 400 when no JSON body is provided")
        void createClient_NoJsonBody_Returns400() throws Exception {
            mockMvc.perform(post("/api/clients").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when invalid JSON is provided")
        void createClient_InvalidJson_Returns400() throws Exception {
            mockMvc.perform(post("/api/clients").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidClient)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 201 when valid JSON is provided")
        void createClient_ValidJson_Returns201() throws Exception {
            mockMvc.perform(post("/api/clients").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validClient)))
                    .andExpect(status().isCreated()).andExpect(jsonPath("$.id", notNullValue()))
                    .andExpect(jsonPath("$.firstName", is("Hop")))
                    .andExpect(jsonPath("$.lastName", is("Pod")))
                    .andExpect(jsonPath("$.emailAddress", is("hippityhoppity@hoparound.com")));
        }

        @Test
        @DisplayName("Should return 400 when client has non-null ID")
        void createClient_WithId_Returns400() throws Exception {
            validClient.setId("some-id");

            mockMvc.perform(post("/api/clients").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validClient)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Update Client Tests")
    class UpdateClientTests {

        @Test
        @DisplayName("Should return 404 when target ID is blank")
        void updateClient_BlankId_Returns404() throws Exception {
            mockMvc.perform(put("/api/clients/").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateClient)))
                    .andExpect(status().isNotFound()); // Should be 404 for malformed URL
        }

        @Test
        @DisplayName("Should return 404 when client does not exist")
        void updateClient_NonExistingId_Returns404() throws Exception {
            String nonExistingId = UUID.randomUUID().toString();

            mockMvc.perform(
                    put("/api/clients/{id}", nonExistingId).contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateClient)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 when source data is invalid")
        void updateClient_InvalidSource_Returns400() throws Exception {
            // First create a client using the service (proper way)
            Client savedClient = clientService.createClient(validClient);

            // Try to update with invalid data (client with ID set)
            invalidClient.setId("should-be-null");

            mockMvc.perform(put("/api/clients/{id}", savedClient.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidClient)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 200 when update is successful")
        void updateClient_ValidSource_Returns200() throws Exception {
            // First create a client using service
            Client savedClient = clientService.createClient(validClient);

            mockMvc.perform(put("/api/clients/{id}", savedClient.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateClient)))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.id", is(savedClient.getId())))
                    .andExpect(jsonPath("$.firstName", is("UpdatedName")))
                    .andExpect(jsonPath("$.lastName", is("Scotch")))
                    .andExpect(jsonPath("$.city", is("Zurich")));
        }
    }

    @Nested
    @DisplayName("Get Client Tests")
    class GetClientTests {

        @Test
        @DisplayName("Should return 404 when ID is blank")
        void getClient_BlankId_Returns404() throws Exception {
            mockMvc.perform(get("/api/clients/")).andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 when client does not exist")
        void getClient_NonExistingId_Returns404() throws Exception {
            String nonExistingId = UUID.randomUUID().toString();

            mockMvc.perform(get("/api/clients/{id}", nonExistingId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 200 when client exists")
        void getClient_ExistingId_Returns200() throws Exception {
            // First create a client using service
            Client savedClient = clientService.createClient(validClient);

            mockMvc.perform(get("/api/clients/{id}", savedClient.getId()))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.id", is(savedClient.getId())))
                    .andExpect(jsonPath("$.firstName", is("Hop")))
                    .andExpect(jsonPath("$.lastName", is("Pod")))
                    .andExpect(jsonPath("$.emailAddress", is("hippityhoppity@hoparound.com")));
        }
    }

    @Nested
    @DisplayName("Delete Client Tests")
    class DeleteClientTests {

        @Test
        @DisplayName("Should return 404 when ID is blank")
        void deleteClient_BlankId_Returns404() throws Exception {
            mockMvc.perform(delete("/api/clients/")).andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 when client does not exist")
        void deleteClient_NonExistingId_Returns404() throws Exception {
            String nonExistingId = UUID.randomUUID().toString();

            mockMvc.perform(delete("/api/clients/{id}", nonExistingId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 204 when deletion is successful")
        void deleteClient_ExistingId_Returns204() throws Exception {
            // First create a client using service
            Client savedClient = clientService.createClient(validClient);

            mockMvc.perform(delete("/api/clients/{id}", savedClient.getId()))
                    .andExpect(status().isNoContent());

            // Verify client is deleted
            mockMvc.perform(get("/api/clients/{id}", savedClient.getId()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Health Check Tests")
    class HealthCheckTests {

        @Test
        @DisplayName("Should return 200 when service is healthy")
        void healthCheck_ServiceHealthy_Returns200() throws Exception {
            mockMvc.perform(get("/health")).andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", is("UP")))
                    .andExpect(jsonPath("$.database", is("UP")))
                    .andExpect(jsonPath("$.service", is("client-service")))
                    .andExpect(jsonPath("$.timestamp", notNullValue()));
        }
    }
}
