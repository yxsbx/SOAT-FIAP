package br.com.autocarehub.interfaces.rest.impl;

import br.com.autocarehub.interfaces.rest.generated.api.ServiceOrdersApi;
import br.com.autocarehub.interfaces.rest.generated.model.AddServiceOrderPartRequest;
import br.com.autocarehub.interfaces.rest.generated.model.AddServiceOrderServiceRequest;
import br.com.autocarehub.interfaces.rest.generated.model.CreateServiceOrderRequest;
import br.com.autocarehub.interfaces.rest.generated.model.ServiceOrderListResponse;
import br.com.autocarehub.interfaces.rest.generated.model.ServiceOrderResponse;
import br.com.autocarehub.interfaces.rest.generated.model.ServiceOrderStatus;
import br.com.autocarehub.interfaces.rest.generated.model.UpdateServiceOrderStatusRequest;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceOrdersController implements ServiceOrdersApi {

    @Override
    public ResponseEntity<ServiceOrderResponse> addPartToServiceOrder(UUID serviceOrderId, AddServiceOrderPartRequest addServiceOrderPartRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> addServiceToServiceOrder(UUID serviceOrderId, AddServiceOrderServiceRequest addServiceOrderServiceRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> approveServiceOrderBudget(UUID serviceOrderId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> createServiceOrder(CreateServiceOrderRequest createServiceOrderRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> generateServiceOrderBudget(UUID serviceOrderId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> getServiceOrderById(UUID serviceOrderId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<ServiceOrderListResponse> listServiceOrders(Integer page, Integer size, ServiceOrderStatus status) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<ServiceOrderListResponse> listServiceOrdersByCustomer(UUID customerId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> updateServiceOrderStatus(UUID serviceOrderId, UpdateServiceOrderStatusRequest updateServiceOrderStatusRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
