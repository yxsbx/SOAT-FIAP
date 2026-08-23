package br.com.autocarehub.interfaces.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ServiceOrderFlowIntegrationTest {

    private static final String EXTERNAL_SERVICE_TOKEN = "test-external-service-token";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldExecuteServiceOrderFlow() throws Exception {
        String token = login();
        int completedOrdersBefore = getCompletedOrders(token);
        UUID customerId = createCustomer(token);
        UUID vehicleId = createVehicle(token, customerId);
        UUID partId = createPart(token);
        UUID serviceId = createWorkshopService(token);
        UUID serviceOrderId = createServiceOrder(token, vehicleId, serviceId);

        getServiceOrderStatus(token, serviceOrderId, "RECEIVED");
        trackServiceOrder(token, serviceOrderId, "RECEBIDA");
        addServiceToServiceOrder(token, serviceOrderId, serviceId);
        addPartToServiceOrder(token, serviceOrderId, partId);
        generateBudget(token, serviceOrderId);
        getServiceOrderStatus(token, serviceOrderId, "WAITING_APPROVAL");
        approveBudget(token, serviceOrderId);
        updateStatus(token, serviceOrderId);
        finishServiceOrder(token, serviceOrderId);
        deliverServiceOrder(token, serviceOrderId);
        getServiceOrderStatus(token, serviceOrderId, "DELIVERED");
        trackServiceOrder(token, serviceOrderId, "ENTREGUE");
        getAverageExecutionTime(token, completedOrdersBefore + 1);
    }

    @Test
    void shouldReceiveExternalBudgetDecisionAndExternalStatusUpdate() throws Exception {
        String token = login();
        UUID serviceOrderId = createSeedServiceOrderWithBudget(token);

        mockMvc.perform(post("/api/v1/service-orders/{serviceOrderId}/budget/decision", serviceOrderId)
                        .header("Authorization", bearer(token))
                        .header("X-External-Service-Token", EXTERNAL_SERVICE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "decision", "REJECTED",
                                "source", "email",
                                "reason", "Cliente pediu revisão do orçamento"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_DIAGNOSIS"))
                .andExpect(jsonPath("$.approvedAt").doesNotExist());

        mockMvc.perform(post("/api/v1/service-orders/{serviceOrderId}/status/external", serviceOrderId)
                        .header("Authorization", bearer(token))
                        .header("X-External-Service-Token", EXTERNAL_SERVICE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "status", "WAITING_APPROVAL",
                                "source", "email",
                                "message", "Orçamento revisado por ferramenta externa"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WAITING_APPROVAL"));
    }

    @Test
    void shouldReceiveExplicitExternalBudgetApprovalAndRejection() throws Exception {
        String token = login();
        UUID approvedOrderId = createSeedServiceOrderWithBudgetWithoutParts(token);
        UUID rejectedOrderId = createSeedServiceOrderWithBudgetWithoutParts(token);

        mockMvc.perform(post("/api/v1/service-orders/{serviceOrderId}/budget/external-approval", approvedOrderId)
                        .header("Authorization", bearer(token))
                        .header("X-External-Service-Token", EXTERNAL_SERVICE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("source", "email", "reason", "Cliente aprovou pelo webhook"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(approvedOrderId.toString()))
                .andExpect(jsonPath("$.status").value("WAITING_APPROVAL"))
                .andExpect(jsonPath("$.approvedAt").isNotEmpty());

        mockMvc.perform(post("/api/v1/service-orders/{serviceOrderId}/budget/external-rejection", rejectedOrderId)
                        .header("Authorization", bearer(token))
                        .header("X-External-Service-Token", EXTERNAL_SERVICE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("source", "email", "reason", "Cliente pediu revisão"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rejectedOrderId.toString()))
                .andExpect(jsonPath("$.status").value("IN_DIAGNOSIS"))
                .andExpect(jsonPath("$.approvedAt").doesNotExist());

        mockMvc.perform(patch("/api/v1/service-orders/{serviceOrderId}/status", rejectedOrderId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "IN_PROGRESS"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectInvalidExternalBudgetAndStatusNotifications() throws Exception {
        String token = login();
        UUID receivedOrderId = createSeedServiceOrderWithoutBudget(token);

        mockMvc.perform(post("/api/v1/service-orders/{serviceOrderId}/budget/external-approval", UUID.randomUUID())
                        .header("Authorization", bearer(token))
                        .header("X-External-Service-Token", EXTERNAL_SERVICE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("source", "email"))))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/v1/service-orders/{serviceOrderId}/budget/external-approval", receivedOrderId)
                        .header("Authorization", bearer(token))
                        .header("X-External-Service-Token", EXTERNAL_SERVICE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("source", "email"))))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/service-orders/{serviceOrderId}/budget/external-rejection", receivedOrderId)
                        .header("Authorization", bearer(token))
                        .header("X-External-Service-Token", EXTERNAL_SERVICE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("reason", "sem origem"))))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/service-orders/{serviceOrderId}/budget/external-approval", receivedOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("source", "email"))))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/service-orders/{serviceOrderId}/status/external", UUID.randomUUID())
                        .header("Authorization", bearer(token))
                        .header("X-External-Service-Token", EXTERNAL_SERVICE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "IN_DIAGNOSIS", "source", "email"))))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/v1/service-orders/{serviceOrderId}/status/external", receivedOrderId)
                        .header("Authorization", bearer(token))
                        .header("X-External-Service-Token", EXTERNAL_SERVICE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "IN_PROGRESS", "source", "email"))))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/service-orders/{serviceOrderId}/status/external", receivedOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "IN_DIAGNOSIS", "source", "email"))))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/service-orders/{serviceOrderId}/status/external", receivedOrderId)
                        .header("Authorization", bearer(token))
                        .header("X-External-Service-Token", "invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "IN_DIAGNOSIS", "source", "email"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldListOperationalQueueByPriorityAndOldestFirstThroughApi() throws Exception {
        String token = login();
        String response = mockMvc.perform(get("/api/v1/service-orders")
                        .param("page", "0")
                        .param("size", "50")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode items = objectMapper.readTree(response).get("items");
        List<Integer> priorities = new ArrayList<>();
        for (JsonNode item : items) {
            String status = item.get("status").asText();
            org.assertj.core.api.Assertions.assertThat(status).isNotIn("FINISHED", "DELIVERED");
            priorities.add(priority(status));
        }
        org.assertj.core.api.Assertions.assertThat(priorities).isSorted();
    }

    private String login() throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("username", "admin@autocarehub.com", "password", "autocare123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("accessToken").asText();
    }

    private UUID createCustomer(String token) throws Exception {
        String response = mockMvc.perform(post("/api/v1/customers")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name",
                                "Maria Silva",
                                "document",
                                "52998224725",
                                "phone",
                                "11999999999",
                                "email",
                                "maria@example.com",
                                "address",
                                Map.of(
                                        "street", "Avenida Paulista",
                                        "number", "1000",
                                        "neighborhood", "Bela Vista",
                                        "city", "São Paulo",
                                        "state", "SP",
                                        "zipCode", "01310-100")))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return uuid(response);
    }

    private UUID createVehicle(String token, UUID customerId) throws Exception {
        String response = mockMvc.perform(post("/api/v1/vehicles")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "customerId",
                                customerId,
                                "plate",
                                "ABC1D23",
                                "brand",
                                "Honda",
                                "model",
                                "Civic",
                                "year",
                                2020,
                                "mileage",
                                30000))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return uuid(response);
    }

    private UUID createPart(String token) throws Exception {
        String response = mockMvc.perform(post("/api/v1/parts")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", "Oil filter",
                                "description", "Filtro de oleo do motor",
                                "sku", "OIL-INT-001",
                                "category", "Filters",
                                "subcategory", "Oil",
                                "brand", "Bosch",
                                "unitPrice", 50.00,
                                "stockQuantity", 10,
                                "minimumStock", 2))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return uuid(response);
    }

    private UUID createWorkshopService(String token) throws Exception {
        String response = mockMvc.perform(post("/api/v1/workshop-services")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name",
                                "Oil change",
                                "description",
                                "Oil and filter replacement",
                                "basePrice",
                                100.00,
                                "estimatedTimeInMinutes",
                                60))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return uuid(response);
    }

    private UUID createServiceOrder(String token, UUID vehicleId, UUID serviceId) throws Exception {
        String response = mockMvc.perform(post("/api/v1/service-orders")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "customerDocument",
                                "52998224725",
                                "vehicleId",
                                vehicleId,
                                "diagnosticNotes",
                                "Customer reports engine noise",
                                "services",
                                java.util.List.of(Map.of("serviceId", serviceId, "quantity", 1)),
                                "generateBudget",
                                false))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("RECEIVED"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        return uuid(response);
    }

    private UUID createSeedServiceOrderWithBudget(String token) throws Exception {
        String response = mockMvc.perform(post("/api/v1/service-orders")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "customerDocument",
                                "12345678909",
                                "vehicleId",
                                "20000000-0000-0000-0000-000000000001",
                                "diagnosticNotes",
                                "Cliente solicita revisão de freios",
                                "services",
                                java.util.List.of(
                                        Map.of("serviceId", "30000000-0000-0000-0000-000000000004", "quantity", 1)),
                                "parts",
                                java.util.List.of(
                                        Map.of("partId", "40000000-0000-0000-0000-000000000005", "quantity", 1)),
                                "generateBudget",
                                true))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("WAITING_APPROVAL"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        return uuid(response);
    }

    private UUID createSeedServiceOrderWithBudgetWithoutParts(String token) throws Exception {
        return createSeedServiceOrder(token, true);
    }

    private UUID createSeedServiceOrderWithoutBudget(String token) throws Exception {
        return createSeedServiceOrder(token, false);
    }

    private UUID createSeedServiceOrder(String token, boolean generateBudget) throws Exception {
        String response = mockMvc.perform(post("/api/v1/service-orders")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "customerDocument",
                                "12345678909",
                                "vehicleId",
                                "20000000-0000-0000-0000-000000000001",
                                "diagnosticNotes",
                                "Cliente solicita revisão geral",
                                "services",
                                java.util.List.of(
                                        Map.of("serviceId", "30000000-0000-0000-0000-000000000004", "quantity", 1)),
                                "generateBudget",
                                generateBudget))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return uuid(response);
    }

    private void addServiceToServiceOrder(String token, UUID serviceOrderId, UUID serviceId) throws Exception {
        mockMvc.perform(post("/api/v1/service-orders/{serviceOrderId}/services", serviceOrderId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("serviceId", serviceId, "quantity", 2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.services[0].serviceId").value(serviceId.toString()));
    }

    private void addPartToServiceOrder(String token, UUID serviceOrderId, UUID partId) throws Exception {
        mockMvc.perform(post("/api/v1/service-orders/{serviceOrderId}/parts", serviceOrderId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("partId", partId, "quantity", 4))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parts[0].partId").value(partId.toString()));
    }

    private void generateBudget(String token, UUID serviceOrderId) throws Exception {
        mockMvc.perform(post("/api/v1/service-orders/{serviceOrderId}/budget/generate", serviceOrderId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WAITING_APPROVAL"))
                .andExpect(jsonPath("$.servicesTotal").value(300.00))
                .andExpect(jsonPath("$.partsTotal").value(200.00))
                .andExpect(jsonPath("$.totalAmount").value(500.00));
    }

    private void approveBudget(String token, UUID serviceOrderId) throws Exception {
        mockMvc.perform(post("/api/v1/service-orders/{serviceOrderId}/budget/approve", serviceOrderId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approvedAt").isNotEmpty());
    }

    private void getServiceOrderStatus(String token, UUID serviceOrderId, String expectedStatus) throws Exception {
        mockMvc.perform(get("/api/v1/service-orders/{serviceOrderId}", serviceOrderId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(serviceOrderId.toString()))
                .andExpect(jsonPath("$.status").value(expectedStatus));
    }

    private void trackServiceOrder(String token, UUID serviceOrderId, String expectedStatus) throws Exception {
        mockMvc.perform(get("/api/v1/service-orders/tracking")
                        .param("serviceOrderId", serviceOrderId.toString())
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(serviceOrderId.toString()))
                .andExpect(jsonPath("$.items[0].status").value(expectedStatus));
    }

    private void updateStatus(String token, UUID serviceOrderId) throws Exception {
        mockMvc.perform(patch("/api/v1/service-orders/{serviceOrderId}/status", serviceOrderId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "IN_PROGRESS"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    private void finishServiceOrder(String token, UUID serviceOrderId) throws Exception {
        mockMvc.perform(patch("/api/v1/service-orders/{serviceOrderId}/status", serviceOrderId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "FINISHED"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINISHED"));
    }

    private void deliverServiceOrder(String token, UUID serviceOrderId) throws Exception {
        mockMvc.perform(patch("/api/v1/service-orders/{serviceOrderId}/status", serviceOrderId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "DELIVERED"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }

    private int getCompletedOrders(String token) throws Exception {
        String response = mockMvc.perform(get("/api/v1/service-orders/metrics/average-execution-time")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("completedOrders").asInt();
    }

    private void getAverageExecutionTime(String token, int expectedCompletedOrders) throws Exception {
        mockMvc.perform(get("/api/v1/service-orders/metrics/average-execution-time")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completedOrders").value(expectedCompletedOrders))
                .andExpect(jsonPath("$.averageExecutionTimeInMinutes").isNumber());
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    private UUID uuid(String response) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(response);
        return UUID.fromString(jsonNode.get("id").asText());
    }

    private int priority(String status) {
        return switch (status) {
            case "IN_PROGRESS" -> 0;
            case "WAITING_APPROVAL" -> 1;
            case "IN_DIAGNOSIS" -> 2;
            case "RECEIVED" -> 3;
            default -> 4;
        };
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
