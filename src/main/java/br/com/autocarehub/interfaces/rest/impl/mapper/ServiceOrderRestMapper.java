package br.com.autocarehub.interfaces.rest.impl.mapper;

import br.com.autocarehub.application.usecase.serviceorder.*;
import br.com.autocarehub.domain.ServiceOrder;
import br.com.autocarehub.interfaces.rest.generated.model.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public final class ServiceOrderRestMapper {

    private ServiceOrderRestMapper() {
    }

    public static CreateServiceOrderUseCase.Command toCommand(CreateServiceOrderRequest request) {
        return new CreateServiceOrderUseCase.Command(
                request.getCustomerDocument(),
                toCustomerInput(request.getCustomer()),
                request.getVehicleId(),
                toVehicleInput(request.getVehicle()),
                request.getDiagnosticNotes(),
                request.getServices().stream().map(ServiceOrderRestMapper::toServiceInput).toList(),
                request.getParts() == null
                        ? List.of()
                        : request.getParts().stream().map(ServiceOrderRestMapper::toPartInput).toList(),
                request.getGenerateBudget() == null || Boolean.TRUE.equals(request.getGenerateBudget()));
    }

    public static AddServiceToServiceOrderUseCase.Command toCommand(
            UUID serviceOrderId, AddServiceOrderServiceRequest request) {
        return new AddServiceToServiceOrderUseCase.Command(
                serviceOrderId, request.getServiceId(), request.getQuantity());
    }

    public static AddPartToServiceOrderUseCase.Command toCommand(
            UUID serviceOrderId, AddServiceOrderPartRequest request) {
        return new AddPartToServiceOrderUseCase.Command(
                serviceOrderId, request.getPartId(), request.getQuantity());
    }

    public static UpdateServiceOrderStatusUseCase.Command toCommand(
            UUID serviceOrderId, UpdateServiceOrderStatusRequest request) {
        return new UpdateServiceOrderStatusUseCase.Command(
                serviceOrderId,
                br.com.autocarehub.domain.ServiceOrderStatus.fromExternalCode(
                        request.getStatus().getValue()));
    }

    public static ListServiceOrdersUseCase.Query toQuery(
            br.com.autocarehub.interfaces.rest.generated.model.ServiceOrderStatus status,
            UUID customerId,
            UUID vehicleId,
            OffsetDateTime createdFrom,
            OffsetDateTime createdTo) {
        return new ListServiceOrdersUseCase.Query(
                status == null
                        ? null
                        : br.com.autocarehub.domain.ServiceOrderStatus.fromExternalCode(status.getValue()),
                customerId,
                vehicleId,
                createdFrom == null ? null : createdFrom.toLocalDateTime(),
                createdTo == null ? null : createdTo.toLocalDateTime());
    }

    public static ServiceOrderResponse toResponse(ServiceOrder serviceOrder) {
        return new ServiceOrderResponse(
                serviceOrder.id(),
                serviceOrder.customerId(),
                serviceOrder.vehicleId(),
                br.com.autocarehub.interfaces.rest.generated.model.ServiceOrderStatus.fromValue(
                        serviceOrder.status().externalCode()),
                serviceOrder.diagnosticNotes(),
                serviceOrder.services().stream().map(ServiceOrderRestMapper::toServiceItem).toList(),
                serviceOrder.parts().stream().map(ServiceOrderRestMapper::toPartItem).toList(),
                serviceOrder.servicesTotal().value().doubleValue(),
                serviceOrder.partsTotal().value().doubleValue(),
                serviceOrder.totalAmount().value().doubleValue(),
                RestMapperSupport.toOffsetDateTime(serviceOrder.createdAt()))
                .budgetGeneratedAt(RestMapperSupport.toOffsetDateTime(serviceOrder.budgetGeneratedAt()))
                .approvedAt(RestMapperSupport.toOffsetDateTime(serviceOrder.approvedAt()))
                .startedAt(RestMapperSupport.toOffsetDateTime(serviceOrder.startedAt()))
                .finishedAt(RestMapperSupport.toOffsetDateTime(serviceOrder.finishedAt()))
                .deliveredAt(RestMapperSupport.toOffsetDateTime(serviceOrder.deliveredAt()));
    }

    public static ServiceOrderListResponse toListResponse(
            List<ServiceOrder> serviceOrders, Integer page, Integer size) {
        return new ServiceOrderListResponse(
                RestMapperSupport.page(serviceOrders, page, size).stream()
                        .map(ServiceOrderRestMapper::toResponse)
                        .toList());
    }

    public static ServiceOrderTrackingListResponse toTrackingListResponse(
            List<TrackServiceOrderUseCase.Output> outputs) {
        return new ServiceOrderTrackingListResponse(
                outputs.stream().map(ServiceOrderRestMapper::toTrackingResponse).toList());
    }

    public static AverageExecutionTimeResponse toResponse(
            GetAverageServiceOrderExecutionTimeUseCase.Output output) {
        return new AverageExecutionTimeResponse(
                output.completedOrders(), output.averageExecutionTimeInMinutes());
    }

    private static ServiceOrderServiceItem toServiceItem(ServiceOrder.ServiceOrderService service) {
        return new ServiceOrderServiceItem(
                service.serviceId(),
                service.name(),
                service.quantity(),
                service.unitPrice().value().doubleValue(),
                service.totalPrice().value().doubleValue());
    }

    private static ServiceOrderPartItem toPartItem(ServiceOrder.ServiceOrderPart part) {
        return new ServiceOrderPartItem(
                part.partId(),
                part.name(),
                part.sku(),
                part.quantity(),
                part.unitPrice().value().doubleValue(),
                part.totalPrice().value().doubleValue());
    }

    private static ServiceOrderTrackingResponse toTrackingResponse(
            TrackServiceOrderUseCase.Output output) {
        ServiceOrder serviceOrder = output.serviceOrder();
        return new ServiceOrderTrackingResponse(
                serviceOrder.id(),
                serviceOrder.customerId(),
                VehicleRestMapper.toResponse(output.vehicle()),
                ServiceOrderTrackingStatus.fromValue(serviceOrder.status().name()),
                serviceOrder.diagnosticNotes(),
                serviceOrder.services().stream().map(ServiceOrderRestMapper::toServiceItem).toList(),
                serviceOrder.parts().stream().map(ServiceOrderRestMapper::toPartItem).toList(),
                toBudgetTrackingResponse(serviceOrder),
                statusHistory(serviceOrder),
                RestMapperSupport.toOffsetDateTime(serviceOrder.createdAt()));
    }

    private static ServiceOrderBudgetTrackingResponse toBudgetTrackingResponse(
            ServiceOrder serviceOrder) {
        return new ServiceOrderBudgetTrackingResponse(
                        serviceOrder.budgetGeneratedAt() != null,
                        serviceOrder.approvedAt() != null,
                        serviceOrder.servicesTotal().value().doubleValue(),
                        serviceOrder.partsTotal().value().doubleValue(),
                        serviceOrder.totalAmount().value().doubleValue())
                .generatedAt(RestMapperSupport.toOffsetDateTime(serviceOrder.budgetGeneratedAt()))
                .approvedAt(RestMapperSupport.toOffsetDateTime(serviceOrder.approvedAt()));
    }

    private static List<ServiceOrderStatusHistoryItem> statusHistory(ServiceOrder serviceOrder) {
        java.util.ArrayList<ServiceOrderStatusHistoryItem> history = new java.util.ArrayList<>();
        history.add(
                statusHistoryItem(
                        br.com.autocarehub.domain.ServiceOrderStatus.RECEBIDA,
                        serviceOrder.createdAt(),
                        "Ordem de servico criada"));
        if (serviceOrder.status() == br.com.autocarehub.domain.ServiceOrderStatus.EM_DIAGNOSTICO) {
            history.add(
                    statusHistoryItem(
                            br.com.autocarehub.domain.ServiceOrderStatus.EM_DIAGNOSTICO,
                            serviceOrder.createdAt(),
                            "Diagnostico iniciado"));
        }
        if (serviceOrder.budgetGeneratedAt() != null) {
            history.add(
                    statusHistoryItem(
                            br.com.autocarehub.domain.ServiceOrderStatus.EM_DIAGNOSTICO,
                            serviceOrder.createdAt(),
                            "Diagnostico realizado"));
            history.add(
                    statusHistoryItem(
                            br.com.autocarehub.domain.ServiceOrderStatus.AGUARDANDO_APROVACAO,
                            serviceOrder.budgetGeneratedAt(),
                            "Orcamento gerado e disponibilizado para aprovacao"));
        }
        if (serviceOrder.approvedAt() != null) {
            history.add(
                    statusHistoryItem(
                            br.com.autocarehub.domain.ServiceOrderStatus.AGUARDANDO_APROVACAO,
                            serviceOrder.approvedAt(),
                            "Orcamento aprovado pelo cliente"));
        }
        if (serviceOrder.startedAt() != null) {
            history.add(
                    statusHistoryItem(
                            br.com.autocarehub.domain.ServiceOrderStatus.EM_EXECUCAO,
                            serviceOrder.startedAt(),
                            "Execucao iniciada"));
        }
        if (serviceOrder.finishedAt() != null) {
            history.add(
                    statusHistoryItem(
                            br.com.autocarehub.domain.ServiceOrderStatus.FINALIZADA,
                            serviceOrder.finishedAt(),
                            "Servico finalizado"));
        }
        if (serviceOrder.deliveredAt() != null) {
            history.add(
                    statusHistoryItem(
                            br.com.autocarehub.domain.ServiceOrderStatus.ENTREGUE,
                            serviceOrder.deliveredAt(),
                            "Veiculo entregue"));
        }
        return history;
    }

    private static ServiceOrderStatusHistoryItem statusHistoryItem(
            br.com.autocarehub.domain.ServiceOrderStatus status,
            java.time.LocalDateTime occurredAt,
            String description) {
        return new ServiceOrderStatusHistoryItem(
                ServiceOrderTrackingStatus.fromValue(status.name()),
                RestMapperSupport.toOffsetDateTime(occurredAt),
                description);
    }

    private static CreateServiceOrderUseCase.CustomerInput toCustomerInput(
            CreateServiceOrderCustomerRequest customer) {
        if (customer == null) {
            return null;
        }
        return new CreateServiceOrderUseCase.CustomerInput(
                customer.getName(),
                customer.getPhone(),
                customer.getEmail(),
                CustomerRestMapper.toDomainAddress(customer.getAddress()));
    }

    private static CreateServiceOrderUseCase.VehicleInput toVehicleInput(
            CreateServiceOrderVehicleRequest vehicle) {
        if (vehicle == null) {
            return null;
        }
        return new CreateServiceOrderUseCase.VehicleInput(
                vehicle.getPlate(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getMileage() == null ? 0 : vehicle.getMileage());
    }

    private static CreateServiceOrderUseCase.ServiceInput toServiceInput(
            CreateServiceOrderServiceRequest service) {
        return new CreateServiceOrderUseCase.ServiceInput(service.getServiceId(), service.getQuantity());
    }

    private static CreateServiceOrderUseCase.PartInput toPartInput(CreateServiceOrderPartRequest part) {
        return new CreateServiceOrderUseCase.PartInput(part.getPartId(), part.getQuantity());
    }
}
