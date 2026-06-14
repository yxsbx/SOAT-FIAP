package br.com.autocarehub.application.usecase.serviceorder;

import br.com.autocarehub.application.exception.ResourceNotFoundException;
import br.com.autocarehub.application.port.out.PartRepository;
import br.com.autocarehub.application.port.out.ServiceOrderRepository;
import br.com.autocarehub.domain.model.ServiceOrder;
import java.util.UUID;

public class GenerateServiceOrderBudgetUseCase {

  private final ServiceOrderRepository serviceOrderRepository;

    public GenerateServiceOrderBudgetUseCase(
      ServiceOrderRepository serviceOrderRepository, PartRepository partRepository) {
    this.serviceOrderRepository = serviceOrderRepository;
    }

  public ServiceOrder execute(UUID serviceOrderId) {
    ServiceOrder serviceOrder =
        serviceOrderRepository
            .findById(serviceOrderId)
            .orElseThrow(() -> new ResourceNotFoundException("Service order not found"));
      serviceOrder.budgetGeneratedAt();
      return serviceOrder;
  }
}
