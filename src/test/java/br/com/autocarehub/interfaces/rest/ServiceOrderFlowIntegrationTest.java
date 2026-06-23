package br.com.autocarehub.interfaces.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        UUID serviceOrderId = createServiceOrder(token, customerId, vehicleId, serviceId);

        addServiceToServiceOrder(token, serviceOrderId, serviceId);
        addPartToServiceOrder(token, serviceOrderId, partId);
        generateBudget(token, serviceOrderId);
        approveBudget(token, serviceOrderId);
        updateStatus(token, serviceOrderId);
        finishServiceOrder(token, serviceOrderId);
        getAverageExecutionTime(token, completedOrdersBefore + 1);
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

    private UUID createServiceOrder(String token, UUID customerId, UUID vehicleId, UUID serviceId) throws Exception {
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

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
