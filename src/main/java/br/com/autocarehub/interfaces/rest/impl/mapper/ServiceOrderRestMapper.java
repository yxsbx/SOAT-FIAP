package br.com.autocarehub.interfaces.rest.impl.mapper;

import br.com.autocarehub.application.usecase.serviceorder.AddPartToServiceOrderUseCase;
import br.com.autocarehub.application.usecase.serviceorder.AddServiceToServiceOrderUseCase;
import br.com.autocarehub.application.usecase.serviceorder.CreateServiceOrderUseCase;
import br.com.autocarehub.application.usecase.serviceorder.UpdateServiceOrderStatusUseCase;
import br.com.autocarehub.domain.ServiceOrder;
import br.com.autocarehub.interfaces.rest.generated.model.AddServiceOrderPartRequest;
import br.com.autocarehub.interfaces.rest.generated.model.AddServiceOrderServiceRequest;
import br.com.autocarehub.interfaces.rest.generated.model.CreateServiceOrderRequest;
import br.com.autocarehub.interfaces.rest.generated.model.ServiceOrderListResponse;
import br.com.autocarehub.interfaces.rest.generated.model.ServiceOrderPartItem;
import br.com.autocarehub.interfaces.rest.generated.model.ServiceOrderResponse;
import br.com.autocarehub.interfaces.rest.generated.model.ServiceOrderServiceItem;
import br.com.autocarehub.interfaces.rest.generated.model.UpdateServiceOrderStatusRequest;
import java.util.List;
import java.util.UUID;

public final class ServiceOrderRestMapper {

    private ServiceOrderRestMapper() {
    }

    public static CreateServiceOrderUseCase.Command toCommand(CreateServiceOrderRequest request) {
        return new CreateServiceOrderUseCase.Command(
                request.getCustomerId(),
                request.getVehicleId(),
                request.getDiagnosticNotes()
        );
    }

    public static AddServiceToServiceOrderUseCase.Command toCommand(UUID serviceOrderId, AddServiceOrderServiceRequest request) {
        return new AddServiceToServiceOrderUseCase.Command(serviceOrderId, request.getServiceId(), request.getQuantity());
    }

    public static AddPartToServiceOrderUseCase.Command toCommand(UUID serviceOrderId, AddServiceOrderPartRequest request) {
        return new AddPartToServiceOrderUseCase.Command(serviceOrderId, request.getPartId(), request.getQuantity());
    }

    public static UpdateServiceOrderStatusUseCase.Command toCommand(UUID serviceOrderId, UpdateServiceOrderStatusRequest request) {
        return new UpdateServiceOrderStatusUseCase.Command(
                serviceOrderId,
                br.com.autocarehub.domain.ServiceOrderStatus.valueOf(request.getStatus().getValue())
        );
    }

    public static ServiceOrderResponse toResponse(ServiceOrder serviceOrder) {
        return new ServiceOrderResponse(
                serviceOrder.id(),
                serviceOrder.customerId(),
                serviceOrder.vehicleId(),
                br.com.autocarehub.interfaces.rest.generated.model.ServiceOrderStatus.fromValue(serviceOrder.status().name()),
                serviceOrder.diagnosticNotes(),
                serviceOrder.services().stream().map(ServiceOrderRestMapper::toServiceItem).toList(),
                serviceOrder.parts().stream().map(ServiceOrderRestMapper::toPartItem).toList(),
                serviceOrder.totalAmount().value().doubleValue(),
                RestMapperSupport.toOffsetDateTime(serviceOrder.createdAt())
        )
                .budgetGeneratedAt(RestMapperSupport.toOffsetDateTime(serviceOrder.budgetGeneratedAt()))
                .approvedAt(RestMapperSupport.toOffsetDateTime(serviceOrder.approvedAt()))
                .startedAt(RestMapperSupport.toOffsetDateTime(serviceOrder.startedAt()))
                .finishedAt(RestMapperSupport.toOffsetDateTime(serviceOrder.finishedAt()))
                .deliveredAt(RestMapperSupport.toOffsetDateTime(serviceOrder.deliveredAt()));
    }

    public static ServiceOrderListResponse toListResponse(List<ServiceOrder> serviceOrders, Integer page, Integer size) {
        return new ServiceOrderListResponse(RestMapperSupport.page(serviceOrders, page, size).stream()
                .map(ServiceOrderRestMapper::toResponse)
                .toList());
    }

    private static ServiceOrderServiceItem toServiceItem(ServiceOrder.ServiceOrderService service) {
        return new ServiceOrderServiceItem(
                service.serviceId(),
                service.name(),
                service.quantity(),
                service.unitPrice().value().doubleValue(),
                service.totalPrice().value().doubleValue()
        );
    }

    private static ServiceOrderPartItem toPartItem(ServiceOrder.ServiceOrderPart part) {
        return new ServiceOrderPartItem(
                part.partId(),
                part.name(),
                part.sku(),
                part.quantity(),
                part.unitPrice().value().doubleValue(),
                part.totalPrice().value().doubleValue()
        );
    }
}
