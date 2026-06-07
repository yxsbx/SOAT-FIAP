package br.com.autocarehub.interfaces.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAuthorizationIntegrationTest {

  private static final String CUSTOMER_ID = "10000000-0000-0000-0000-000000000001";
  private static final String OTHER_CUSTOMER_ID = "10000000-0000-0000-0000-000000000002";
  private static final String CUSTOMER_ORDER_ID = "50000000-0000-0000-0000-000000000002";
  private static final String OTHER_CUSTOMER_ORDER_ID = "50000000-0000-0000-0000-000000000003";

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void shouldRequireAuthenticationForAdministrativeApis() throws Exception {
    int status = mockMvc.perform(get("/api/v1/customers")).andReturn().getResponse().getStatus();

    assertThat(status).isIn(401, 403);
  }

  @Test
  void shouldAllowAdministrativeApiAccessWithValidAdminJwt() throws Exception {
    String token = login("admin@autocarehub.com");

    mockMvc
        .perform(get("/api/v1/customers").header("Authorization", bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items").isArray());
  }

  @Test
  void shouldBlockCustomerFromAdministrativeApis() throws Exception {
    String token = login("cliente@autocarehub.com");

    mockMvc
        .perform(get("/api/v1/customers").header("Authorization", bearer(token)))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldAllowCustomerToTrackOnlyOwnServiceOrders() throws Exception {
    String token = login("cliente@autocarehub.com");

    mockMvc
        .perform(
            get("/api/v1/customers/{customerId}/service-orders", CUSTOMER_ID)
                .header("Authorization", bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items").isArray());

    mockMvc
        .perform(
            get("/api/v1/customers/{customerId}/service-orders", OTHER_CUSTOMER_ID)
                .header("Authorization", bearer(token)))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldAllowCustomerToApproveOnlyOwnBudget() throws Exception {
    String token = login("cliente@autocarehub.com");

    mockMvc
        .perform(
            post("/api/v1/service-orders/{serviceOrderId}/budget/approve", CUSTOMER_ORDER_ID)
                .header("Authorization", bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.approvedAt").isNotEmpty());

    mockMvc
        .perform(
            post("/api/v1/service-orders/{serviceOrderId}/budget/approve", OTHER_CUSTOMER_ORDER_ID)
                .header("Authorization", bearer(token)))
        .andExpect(status().isForbidden());
  }

  private String login(String username) throws Exception {
    String response =
        mockMvc
            .perform(
                post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(Map.of("username", username, "password", "autocare123"))))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return objectMapper.readTree(response).get("accessToken").asText();
  }

  private String json(Object value) throws Exception {
    return objectMapper.writeValueAsString(value);
  }

  private String bearer(String token) {
    return "Bearer " + token;
  }
}
