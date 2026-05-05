package br.com.autocarehub.interfaces.rest.impl;

import br.com.autocarehub.interfaces.rest.generated.api.CustomersApi;
import br.com.autocarehub.interfaces.rest.generated.model.CreateCustomerRequest;
import br.com.autocarehub.interfaces.rest.generated.model.CustomerListResponse;
import br.com.autocarehub.interfaces.rest.generated.model.CustomerResponse;
import br.com.autocarehub.interfaces.rest.generated.model.UpdateCustomerRequest;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomersController implements CustomersApi {

    @Override
    public ResponseEntity<CustomerResponse> createCustomer(CreateCustomerRequest createCustomerRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<Void> deleteCustomer(UUID customerId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<CustomerResponse> getCustomerById(UUID customerId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<CustomerListResponse> listCustomers(Integer page, Integer size, Boolean active) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<CustomerResponse> updateCustomer(UUID customerId, UpdateCustomerRequest updateCustomerRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
