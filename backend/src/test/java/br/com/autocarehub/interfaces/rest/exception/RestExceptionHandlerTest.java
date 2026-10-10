package br.com.autocarehub.interfaces.rest.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.mock.web.MockHttpServletRequest;

class RestExceptionHandlerTest {
    @Test
    void shouldReturnRetryableConflictWithoutExposingDatabaseDetails() {
        var request = new MockHttpServletRequest("POST", "/api/v1/service-orders/id/budget/decision");
        var response = new RestExceptionHandler()
                .handleConcurrentUpdate(
                        new CannotAcquireLockException("internal database connection details"), request);
        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody().getMessage()).isEqualTo("Concurrent update; retry the request");
        assertThat(response.getBody().getPath()).isEqualTo(request.getRequestURI());
    }
}
