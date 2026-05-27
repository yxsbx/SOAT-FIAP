package br.com.autocarehub.application.usecase.serviceorder;

import br.com.autocarehub.application.ResourceNotFoundException;
import br.com.autocarehub.application.repository.PartRepository;
import br.com.autocarehub.application.repository.ServiceOrderRepository;
import br.com.autocarehub.domain.Part;
import br.com.autocarehub.domain.ServiceOrder;

import java.util.UUID;

public class GenerateServiceOrderBudgetUseCase {

    private final ServiceOrderRepository serviceOrderRepository;
    private final PartRepository partRepository;

    public GenerateServiceOrderBudgetUseCase(
            ServiceOrderRepository serviceOrderRepository, PartRepository partRepository) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.partRepository = partRepository;
    }

    public ServiceOrder execute(UUID serviceOrderId) {
        ServiceOrder serviceOrder =
                serviceOrderRepository
                        .findById(serviceOrderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Service order not found"));
        serviceOrder.generateBudget();
        for (ServiceOrder.ServiceOrderPart orderPart : serviceOrder.parts()) {
            Part part =
                    partRepository
                            .findById(orderPart.partId())
                            .orElseThrow(() -> new ResourceNotFoundException("Part not found"));
            part.reserveStock(orderPart.quantity());
            partRepository.save(part);
        }
        return serviceOrderRepository.save(serviceOrder);
    }
}
