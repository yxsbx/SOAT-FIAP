package br.com.autocarehub.interfaces.rest.impl;

import br.com.autocarehub.application.usecase.serviceorder.AddPartToServiceOrderUseCase;
import br.com.autocarehub.application.usecase.serviceorder.AddServiceToServiceOrderUseCase;
import br.com.autocarehub.application.usecase.serviceorder.ApproveServiceOrderBudgetUseCase;
import br.com.autocarehub.application.usecase.serviceorder.CreateServiceOrderUseCase;
import br.com.autocarehub.application.usecase.serviceorder.FindServiceOrderUseCase;
import br.com.autocarehub.application.usecase.serviceorder.GenerateServiceOrderBudgetUseCase;
import br.com.autocarehub.application.usecase.serviceorder.ListServiceOrdersByCustomerUseCase;
import br.com.autocarehub.application.usecase.serviceorder.ListServiceOrdersUseCase;
import br.com.autocarehub.application.usecase.serviceorder.UpdateServiceOrderStatusUseCase;
import br.com.autocarehub.domain.ServiceOrder;
import br.com.autocarehub.interfaces.rest.generated.api.ServiceOrdersApi;
import br.com.autocarehub.interfaces.rest.generated.model.AddServiceOrderPartRequest;
import br.com.autocarehub.interfaces.rest.generated.model.AddServiceOrderServiceRequest;
import br.com.autocarehub.interfaces.rest.generated.model.CreateServiceOrderRequest;
import br.com.autocarehub.interfaces.rest.generated.model.ServiceOrderListResponse;
import br.com.autocarehub.interfaces.rest.generated.model.ServiceOrderResponse;
import br.com.autocarehub.interfaces.rest.generated.model.ServiceOrderStatus;
import br.com.autocarehub.interfaces.rest.generated.model.UpdateServiceOrderStatusRequest;
import br.com.autocarehub.interfaces.rest.impl.mapper.ServiceOrderRestMapper;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceOrdersController implements ServiceOrdersApi {

    private final CreateServiceOrderUseCase createServiceOrderUseCase;
    private final FindServiceOrderUseCase findServiceOrderUseCase;
    private final ListServiceOrdersUseCase listServiceOrdersUseCase;
    private final AddServiceToServiceOrderUseCase addServiceToServiceOrderUseCase;
    private final AddPartToServiceOrderUseCase addPartToServiceOrderUseCase;
    private final GenerateServiceOrderBudgetUseCase generateServiceOrderBudgetUseCase;
    private final ApproveServiceOrderBudgetUseCase approveServiceOrderBudgetUseCase;
    private final UpdateServiceOrderStatusUseCase updateServiceOrderStatusUseCase;
    private final ListServiceOrdersByCustomerUseCase listServiceOrdersByCustomerUseCase;

    public ServiceOrdersController(CreateServiceOrderUseCase createServiceOrderUseCase, FindServiceOrderUseCase findServiceOrderUseCase, ListServiceOrdersUseCase listServiceOrdersUseCase, AddServiceToServiceOrderUseCase addServiceToServiceOrderUseCase, AddPartToServiceOrderUseCase addPartToServiceOrderUseCase, GenerateServiceOrderBudgetUseCase generateServiceOrderBudgetUseCase, ApproveServiceOrderBudgetUseCase approveServiceOrderBudgetUseCase, UpdateServiceOrderStatusUseCase updateServiceOrderStatusUseCase, ListServiceOrdersByCustomerUseCase listServiceOrdersByCustomerUseCase) {
        this.createServiceOrderUseCase = createServiceOrderUseCase;
        this.findServiceOrderUseCase = findServiceOrderUseCase;
        this.listServiceOrdersUseCase = listServiceOrdersUseCase;
        this.addServiceToServiceOrderUseCase = addServiceToServiceOrderUseCase;
        this.addPartToServiceOrderUseCase = addPartToServiceOrderUseCase;
        this.generateServiceOrderBudgetUseCase = generateServiceOrderBudgetUseCase;
        this.approveServiceOrderBudgetUseCase = approveServiceOrderBudgetUseCase;
        this.updateServiceOrderStatusUseCase = updateServiceOrderStatusUseCase;
        this.listServiceOrdersByCustomerUseCase = listServiceOrdersByCustomerUseCase;
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> addPartToServiceOrder(UUID serviceOrderId, AddServiceOrderPartRequest addServiceOrderPartRequest) {
        ServiceOrder serviceOrder = addPartToServiceOrderUseCase.execute(ServiceOrderRestMapper.toCommand(serviceOrderId, addServiceOrderPartRequest));
        return ResponseEntity.ok(ServiceOrderRestMapper.toResponse(serviceOrder));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> addServiceToServiceOrder(UUID serviceOrderId, AddServiceOrderServiceRequest addServiceOrderServiceRequest) {
        ServiceOrder serviceOrder = addServiceToServiceOrderUseCase.execute(ServiceOrderRestMapper.toCommand(serviceOrderId, addServiceOrderServiceRequest));
        return ResponseEntity.ok(ServiceOrderRestMapper.toResponse(serviceOrder));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> approveServiceOrderBudget(UUID serviceOrderId) {
        return ResponseEntity.ok(ServiceOrderRestMapper.toResponse(approveServiceOrderBudgetUseCase.execute(serviceOrderId)));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> createServiceOrder(CreateServiceOrderRequest createServiceOrderRequest) {
        ServiceOrder serviceOrder = createServiceOrderUseCase.execute(ServiceOrderRestMapper.toCommand(createServiceOrderRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(ServiceOrderRestMapper.toResponse(serviceOrder));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> generateServiceOrderBudget(UUID serviceOrderId) {
        return ResponseEntity.ok(ServiceOrderRestMapper.toResponse(generateServiceOrderBudgetUseCase.execute(serviceOrderId)));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> getServiceOrderById(UUID serviceOrderId) {
        return ResponseEntity.ok(ServiceOrderRestMapper.toResponse(findServiceOrderUseCase.execute(serviceOrderId)));
    }

    @Override
    public ResponseEntity<ServiceOrderListResponse> listServiceOrders(Integer page, Integer size, ServiceOrderStatus status) {
        br.com.autocarehub.domain.ServiceOrderStatus domainStatus = status == null ? null : br.com.autocarehub.domain.ServiceOrderStatus.valueOf(status.getValue());
        return ResponseEntity.ok(ServiceOrderRestMapper.toListResponse(listServiceOrdersUseCase.execute(domainStatus), page, size));
    }

    @Override
    public ResponseEntity<ServiceOrderListResponse> listServiceOrdersByCustomer(UUID customerId) {
        return ResponseEntity.ok(ServiceOrderRestMapper.toListResponse(listServiceOrdersByCustomerUseCase.execute(customerId), null, null));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> updateServiceOrderStatus(UUID serviceOrderId, UpdateServiceOrderStatusRequest updateServiceOrderStatusRequest) {
        ServiceOrder serviceOrder = updateServiceOrderStatusUseCase.execute(ServiceOrderRestMapper.toCommand(serviceOrderId, updateServiceOrderStatusRequest));
        return ResponseEntity.ok(ServiceOrderRestMapper.toResponse(serviceOrder));
    }
}
