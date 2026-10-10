package br.com.autocarehub.application.usecase.serviceorder;

import br.com.autocarehub.application.exception.ResourceNotFoundException;
import br.com.autocarehub.application.port.out.PartRepository;
import br.com.autocarehub.application.port.out.ServiceOrderRepository;
import br.com.autocarehub.domain.model.ServiceOrder;
import java.util.UUID;

public class ApproveServiceOrderBudgetUseCase {

    private final ServiceOrderRepository serviceOrderRepository;
    private final PartRepository partRepository;

    public ApproveServiceOrderBudgetUseCase(
            ServiceOrderRepository serviceOrderRepository, PartRepository partRepository) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.partRepository = partRepository;
    }

    public ServiceOrder execute(UUID serviceOrderId) {
        ServiceOrder serviceOrder = serviceOrderRepository
                .findById(serviceOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Service order not found"));
        if (serviceOrder.approvedAt() != null) {
            return serviceOrder;
        }
        serviceOrder.approveBudget();
        commitReservedParts(serviceOrder);
        return serviceOrderRepository.save(serviceOrder);
    }

    private void commitReservedParts(ServiceOrder serviceOrder) {
        BudgetStock.update(serviceOrder, partRepository, br.com.autocarehub.domain.model.Part::commitReservedStock);
    }
}
