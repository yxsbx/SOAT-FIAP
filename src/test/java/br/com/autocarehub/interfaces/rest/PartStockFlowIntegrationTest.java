package br.com.autocarehub.interfaces.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PartStockFlowIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Test
    void shouldReserveCommitReleaseAndRegisterPartStockMovements() throws Exception {
        String token = login();
        UUID partId = createPart(token);

        reservePart(token, partId, 4);
        commitReservation(token, partId, 3);
        releaseReservation(token, partId, 1);
        registerSale(token, partId, 2);
    }

    private String login() throws Exception {
        String response =
                mockMvc
                        .perform(
                                post("/api/v1/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                json(
                                                        Map.of(
                                                                "username",
                                                                "admin@autocarehub.com",
                                                                "password",
                                                                "autocare123"))))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        return objectMapper.readTree(response).get("accessToken").asText();
    }

    private UUID createPart(String token) throws Exception {
        String sku = "TEST-STOCK-" + UUID.randomUUID();
        String response =
                mockMvc
                        .perform(
                                post("/api/v1/parts")
                                        .header("Authorization", bearer(token))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                json(
                                                        Map.of(
                                                                "name",
                                                                "Sensor de estoque teste",
                                                                "sku",
                                                                sku,
                                                                "category",
                                                                "Teste",
                                                                "subcategory",
                                                                "Integracao",
                                                                "brand",
                                                                "AutoCare",
                                                                "costPrice",
                                                                40.00,
                                                                "unitPrice",
                                                                80.00,
                                                                "stockQuantity",
                                                                10,
                                                                "minimumStock",
                                                                2))))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.stockQuantity").value(10))
                        .andExpect(jsonPath("$.reservedQuantity").value(0))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);
        return UUID.fromString(jsonNode.get("id").asText());
    }

    private void reservePart(String token, UUID partId, int quantity) throws Exception {
        mockMvc
                .perform(
                        patch("/api/v1/parts/{partId}/reserve", partId)
                                .header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(Map.of("quantity", quantity))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(10))
                .andExpect(jsonPath("$.reservedQuantity").value(4))
                .andExpect(jsonPath("$.availableQuantity").value(6));
    }

    private void commitReservation(String token, UUID partId, int quantity) throws Exception {
        mockMvc
                .perform(
                        patch("/api/v1/parts/{partId}/commit-reservation", partId)
                                .header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        json(
                                                Map.of(
                                                        "quantity",
                                                        quantity,
                                                        "reason",
                                                        "Orcamento aprovado em teste"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(7))
                .andExpect(jsonPath("$.reservedQuantity").value(1))
                .andExpect(jsonPath("$.availableQuantity").value(6));
    }

    private void releaseReservation(String token, UUID partId, int quantity) throws Exception {
        mockMvc
                .perform(
                        patch("/api/v1/parts/{partId}/release-reservation", partId)
                                .header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(Map.of("quantity", quantity))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(7))
                .andExpect(jsonPath("$.reservedQuantity").value(0))
                .andExpect(jsonPath("$.availableQuantity").value(7));
    }

    private void registerSale(String token, UUID partId, int quantity) throws Exception {
        mockMvc
                .perform(
                        patch("/api/v1/parts/{partId}/stock-movement", partId)
                                .header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        json(
                                                Map.of(
                                                        "type",
                                                        "SALE",
                                                        "quantity",
                                                        quantity,
                                                        "reason",
                                                        "Venda isolada em teste"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(5))
                .andExpect(jsonPath("$.reservedQuantity").value(0))
                .andExpect(jsonPath("$.availableQuantity").value(5));
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
