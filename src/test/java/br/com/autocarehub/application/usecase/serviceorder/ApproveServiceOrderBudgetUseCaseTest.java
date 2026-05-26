package br.com.autocarehub.application.usecase.serviceorder;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.autocarehub.application.repository.PartRepository;
import br.com.autocarehub.application.repository.ServiceOrderRepository;
import br.com.autocarehub.domain.Money;
import br.com.autocarehub.domain.Part;
import br.com.autocarehub.domain.ServiceOrder;
import br.com.autocarehub.domain.WorkshopService;
import java.util.*;
import org.junit.jupiter.api.Test;

class ApproveServiceOrderBudgetUseCaseTest {

  private final InMemoryServiceOrderRepository serviceOrderRepository =
      new InMemoryServiceOrderRepository();
  private final InMemoryPartRepository partRepository = new InMemoryPartRepository();

  @Test
  void shouldReservePartWhenBudgetIsGeneratedAndReduceStockWhenBudgetIsApproved() {
    Part part = partRepository.save(part());
    ServiceOrder serviceOrder =
        new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Cliente relata vazamento");
    serviceOrder.addService(
        new WorkshopService(
            "Troca de oleo", "Substituição de oleo e filtro", Money.of("100.00"), 60),
        1);
    serviceOrder.addPart(part, 2);
    serviceOrderRepository.save(serviceOrder);

    new GenerateServiceOrderBudgetUseCase(serviceOrderRepository, partRepository)
        .execute(serviceOrder.id());
    assertThat(partRepository.findById(part.id()).orElseThrow().stockQuantity()).isEqualTo(10);
    assertThat(partRepository.findById(part.id()).orElseThrow().reservedQuantity()).isEqualTo(2);

    new ApproveServiceOrderBudgetUseCase(serviceOrderRepository, partRepository)
        .execute(serviceOrder.id());

    Part updated = partRepository.findById(part.id()).orElseThrow();
    assertThat(updated.stockQuantity()).isEqualTo(8);
    assertThat(updated.reservedQuantity()).isZero();
    assertThat(updated.availableQuantity()).isEqualTo(8);
  }

  private static Part part() {
    return new Part(
        "Filtro de oleo",
        "Filtro de oleo do motor",
        "OIL-001",
        "Filtros",
        "Oleo",
        "Bosch",
        Money.of("25.00"),
        Money.of("50.00"),
        10,
        2);
  }

  private static class InMemoryServiceOrderRepository implements ServiceOrderRepository {

    private final Map<UUID, ServiceOrder> serviceOrders = new LinkedHashMap<>();

    @Override
    public ServiceOrder save(ServiceOrder serviceOrder) {
      serviceOrders.put(serviceOrder.id(), serviceOrder);
      return serviceOrder;
    }

    @Override
    public Optional<ServiceOrder> findById(UUID id) {
      return Optional.ofNullable(serviceOrders.get(id));
    }

    @Override
    public List<ServiceOrder> findAll() {
      return List.copyOf(serviceOrders.values());
    }

    @Override
    public List<ServiceOrder> findByCustomerId(UUID customerId) {
      return serviceOrders.values().stream()
          .filter(serviceOrder -> serviceOrder.customerId().equals(customerId))
          .toList();
    }

    @Override
    public List<ServiceOrder> findCompletedWithExecutionTime() {
      return List.of();
    }
  }

  private static class InMemoryPartRepository implements PartRepository {

    private final Map<UUID, Part> parts = new LinkedHashMap<>();

    @Override
    public Part save(Part part) {
      parts.put(part.id(), part);
      return part;
    }

    @Override
    public Optional<Part> findById(UUID id) {
      return Optional.ofNullable(parts.get(id));
    }

    @Override
    public List<Part> findAll() {
      return List.copyOf(parts.values());
    }
  }
}
