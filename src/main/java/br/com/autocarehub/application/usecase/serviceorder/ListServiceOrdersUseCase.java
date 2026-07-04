package br.com.autocarehub.application.usecase.serviceorder;

import br.com.autocarehub.application.port.out.ServiceOrderRepository;
import br.com.autocarehub.domain.enums.ServiceOrderStatus;
import br.com.autocarehub.domain.model.ServiceOrder;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ListServiceOrdersUseCase {

    private final ServiceOrderRepository serviceOrderRepository;

    public ListServiceOrdersUseCase(ServiceOrderRepository serviceOrderRepository) {
        this.serviceOrderRepository = serviceOrderRepository;
    }

    public List<ServiceOrder> execute() {
        return serviceOrderRepository.findAll().stream()
                .filter(ListServiceOrdersUseCase::isVisibleInMainQueue)
                .sorted(operationalQueueOrder())
                .toList();
    }

    public List<ServiceOrder> execute(Query query) {
        return serviceOrderRepository.findAll().stream()
                .filter(ListServiceOrdersUseCase::isVisibleInMainQueue)
                .filter(serviceOrder -> query.status() == null || serviceOrder.status() == query.status())
                .filter(serviceOrder ->
                        query.customerId() == null || serviceOrder.customerId().equals(query.customerId()))
                .filter(serviceOrder ->
                        query.vehicleId() == null || serviceOrder.vehicleId().equals(query.vehicleId()))
                .filter(serviceOrder ->
                        query.createdFrom() == null || !serviceOrder.createdAt().isBefore(query.createdFrom()))
                .filter(serviceOrder ->
                        query.createdTo() == null || !serviceOrder.createdAt().isAfter(query.createdTo()))
                .sorted(operationalQueueOrder())
                .toList();
    }

    private static boolean isVisibleInMainQueue(ServiceOrder serviceOrder) {
        return serviceOrder.status() != ServiceOrderStatus.FINALIZADA
                && serviceOrder.status() != ServiceOrderStatus.ENTREGUE;
    }

    private static Comparator<ServiceOrder> operationalQueueOrder() {
        return Comparator.comparingInt((ServiceOrder serviceOrder) -> statusPriority(serviceOrder.status()))
                .thenComparing(ServiceOrder::createdAt);
    }

    static int statusPriority(ServiceOrderStatus status) {
        return switch (status) {
            case EM_EXECUCAO -> 0;
            case AGUARDANDO_APROVACAO -> 1;
            case EM_DIAGNOSTICO -> 2;
            case RECEBIDA -> 3;
            case FINALIZADA, ENTREGUE -> 4;
        };
    }

    public record Query(
            ServiceOrderStatus status,
            UUID customerId,
            UUID vehicleId,
            LocalDateTime createdFrom,
            LocalDateTime createdTo) {}
}
