package br.com.autocarehub.application.usecase.serviceorder;

import br.com.autocarehub.application.port.out.ServiceOrderRepository;
import br.com.autocarehub.domain.model.ServiceOrder;
import br.com.autocarehub.domain.enums.ServiceOrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ListServiceOrdersUseCase {

  private final ServiceOrderRepository serviceOrderRepository;

  public ListServiceOrdersUseCase(ServiceOrderRepository serviceOrderRepository) {
    this.serviceOrderRepository = serviceOrderRepository;
  }

  public List<ServiceOrder> execute() {
    return serviceOrderRepository.findAll();
  }

  public List<ServiceOrder> execute(Query query) {
    return serviceOrderRepository.findAll().stream()
        .filter(serviceOrder -> query.status() == null || serviceOrder.status() == query.status())
        .filter(
            serviceOrder ->
                query.customerId() == null || serviceOrder.customerId().equals(query.customerId()))
        .filter(
            serviceOrder ->
                query.vehicleId() == null || serviceOrder.vehicleId().equals(query.vehicleId()))
        .filter(
            serviceOrder ->
                query.createdFrom() == null
                    || !serviceOrder.createdAt().isBefore(query.createdFrom()))
        .filter(
            serviceOrder ->
                query.createdTo() == null || !serviceOrder.createdAt().isAfter(query.createdTo()))
        .toList();
  }

  public record Query(
      ServiceOrderStatus status,
      UUID customerId,
      UUID vehicleId,
      LocalDateTime createdFrom,
      LocalDateTime createdTo) {}
}
