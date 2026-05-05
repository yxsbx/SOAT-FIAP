package br.com.autocarehub.application.usecase.serviceorder;

import br.com.autocarehub.application.ResourceNotFoundException;
import br.com.autocarehub.application.repository.ServiceOrderRepository;
import br.com.autocarehub.domain.ServiceOrder;
import java.util.UUID;

public class GenerateServiceOrderBudgetUseCase {

    private final ServiceOrderRepository serviceOrderRepository;

    public GenerateServiceOrderBudgetUseCase(ServiceOrderRepository serviceOrderRepository) {
        this.serviceOrderRepository = serviceOrderRepository;
    }

    public ServiceOrder execute(UUID serviceOrderId) {
        ServiceOrder serviceOrder = serviceOrderRepository.findById(serviceOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Service order not found"));
        serviceOrder.generateBudget();
        return serviceOrderRepository.save(serviceOrder);
    }
}
