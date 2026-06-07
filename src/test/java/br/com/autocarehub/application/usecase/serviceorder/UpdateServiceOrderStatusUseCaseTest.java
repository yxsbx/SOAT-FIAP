package br.com.autocarehub.application.usecase.serviceorder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.autocarehub.application.repository.ServiceOrderRepository;
import br.com.autocarehub.domain.*;
import java.util.*;
import org.junit.jupiter.api.Test;

class UpdateServiceOrderStatusUseCaseTest {

  private final InMemoryServiceOrderRepository serviceOrderRepository =
      new InMemoryServiceOrderRepository();

  private static ServiceOrder serviceOrderWithGeneratedAndApprovedBudget() {
    ServiceOrder serviceOrder = serviceOrderWithGeneratedBudget();
    serviceOrder.approveBudget();
    return serviceOrder;
  }

  private static ServiceOrder serviceOrderWithGeneratedBudget() {
    ServiceOrder serviceOrder =
        new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Cliente relata vazamento");
    serviceOrder.addService(
        new WorkshopService(
            "Troca de oleo", "Substituição de oleo e filtro", Money.of("100.00"), 60),
        1);
    serviceOrder.generateBudget();
    return serviceOrder;
  }

  @Test
  void shouldMoveApprovedBudgetToExecutionFinishedAndDelivered() {
    ServiceOrder serviceOrder = serviceOrderWithGeneratedAndApprovedBudget();
    serviceOrderRepository.save(serviceOrder);
    UpdateServiceOrderStatusUseCase useCase =
        new UpdateServiceOrderStatusUseCase(serviceOrderRepository);

    useCase.execute(
        new UpdateServiceOrderStatusUseCase.Command(
            serviceOrder.id(), ServiceOrderStatus.EM_EXECUCAO));
    useCase.execute(
        new UpdateServiceOrderStatusUseCase.Command(
            serviceOrder.id(), ServiceOrderStatus.FINALIZADA));
    ServiceOrder delivered =
        useCase.execute(
            new UpdateServiceOrderStatusUseCase.Command(
                serviceOrder.id(), ServiceOrderStatus.ENTREGUE));

    assertThat(delivered.status()).isEqualTo(ServiceOrderStatus.ENTREGUE);
    assertThat(delivered.startedAt()).isNotNull();
    assertThat(delivered.finishedAt()).isNotNull();
    assertThat(delivered.deliveredAt()).isNotNull();
  }

  @Test
  void shouldRejectInvalidTransitionBackToReceived() {
    ServiceOrder serviceOrder = serviceOrderWithGeneratedAndApprovedBudget();
    serviceOrderRepository.save(serviceOrder);
    UpdateServiceOrderStatusUseCase useCase =
        new UpdateServiceOrderStatusUseCase(serviceOrderRepository);

    assertThatThrownBy(
            () ->
                useCase.execute(
                    new UpdateServiceOrderStatusUseCase.Command(
                        serviceOrder.id(), ServiceOrderStatus.RECEBIDA)))
        .isInstanceOf(InvalidServiceOrderStatusTransitionException.class)
        .hasMessage("Service order cannot return to received status");
  }

  @Test
  void shouldRejectStartingExecutionBeforeBudgetApproval() {
    ServiceOrder serviceOrder = serviceOrderWithGeneratedBudget();
    serviceOrderRepository.save(serviceOrder);
    UpdateServiceOrderStatusUseCase useCase =
        new UpdateServiceOrderStatusUseCase(serviceOrderRepository);

    assertThatThrownBy(
            () ->
                useCase.execute(
                    new UpdateServiceOrderStatusUseCase.Command(
                        serviceOrder.id(), ServiceOrderStatus.EM_EXECUCAO)))
        .isInstanceOf(DomainException.class)
        .hasMessage("Execution cannot start without budget approval");
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
}
