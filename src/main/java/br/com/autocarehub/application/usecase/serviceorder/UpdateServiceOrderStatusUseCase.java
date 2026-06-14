package br.com.autocarehub.application.usecase.serviceorder;

import br.com.autocarehub.application.ResourceNotFoundException;
import br.com.autocarehub.application.repository.PartRepository;
import br.com.autocarehub.application.repository.ServiceOrderRepository;
import br.com.autocarehub.domain.InvalidServiceOrderStatusTransitionException;
import br.com.autocarehub.domain.Part;
import br.com.autocarehub.domain.ServiceOrder;
import br.com.autocarehub.domain.ServiceOrderStatus;
import java.util.UUID;

public class UpdateServiceOrderStatusUseCase {

  private final ServiceOrderRepository serviceOrderRepository;
  private final PartRepository partRepository;

  public UpdateServiceOrderStatusUseCase(
      ServiceOrderRepository serviceOrderRepository, PartRepository partRepository) {
    this.serviceOrderRepository = serviceOrderRepository;
    this.partRepository = partRepository;
  }

  public ServiceOrder execute(Command command) {
    ServiceOrder serviceOrder =
        serviceOrderRepository
            .findById(command.serviceOrderId())
            .orElseThrow(() -> new ResourceNotFoundException("Service order not found"));
    switch (command.status()) {
      case EM_DIAGNOSTICO -> serviceOrder.startDiagnosis();
      case AGUARDANDO_APROVACAO -> generateBudgetAndReserveParts(serviceOrder);
      case EM_EXECUCAO -> serviceOrder.startExecution();
      case FINALIZADA -> serviceOrder.finish();
      case ENTREGUE -> serviceOrder.deliver();
      case RECEBIDA ->
          throw new InvalidServiceOrderStatusTransitionException(
              "Service order cannot return to received status");
    }
    return serviceOrderRepository.save(serviceOrder);
  }

  private void generateBudgetAndReserveParts(ServiceOrder serviceOrder) {
    if (serviceOrder.budgetGeneratedAt() != null) {
      return;
    }
    serviceOrder.generateBudget();
    for (ServiceOrder.ServiceOrderPart orderPart : serviceOrder.parts()) {
      Part part =
          partRepository
              .findById(orderPart.partId())
              .orElseThrow(() -> new ResourceNotFoundException("Part not found"));
      part.reserveStock(orderPart.quantity());
      partRepository.save(part);
    }
  }

  public record Command(UUID serviceOrderId, ServiceOrderStatus status) {}
}
