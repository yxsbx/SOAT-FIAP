package br.com.autocarehub.application.usecase.serviceorder;

import br.com.autocarehub.application.ApplicationException;
import br.com.autocarehub.application.ResourceNotFoundException;
import br.com.autocarehub.application.repository.ServiceOrderRepository;
import br.com.autocarehub.domain.ServiceOrder;
import br.com.autocarehub.domain.ServiceOrderStatus;
import java.util.UUID;

public class UpdateServiceOrderStatusUseCase {

    private final ServiceOrderRepository serviceOrderRepository;

    public UpdateServiceOrderStatusUseCase(ServiceOrderRepository serviceOrderRepository) {
        this.serviceOrderRepository = serviceOrderRepository;
    }

    public ServiceOrder execute(Command command) {
        ServiceOrder serviceOrder = serviceOrderRepository.findById(command.serviceOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Service order not found"));
        switch (command.status()) {
            case IN_DIAGNOSIS -> serviceOrder.startDiagnosis();
            case WAITING_APPROVAL -> serviceOrder.generateBudget();
            case IN_PROGRESS -> serviceOrder.startExecution();
            case FINISHED -> serviceOrder.finish();
            case DELIVERED -> serviceOrder.deliver();
            case RECEIVED -> throw new ApplicationException("Service order cannot return to received status");
        }
        return serviceOrderRepository.save(serviceOrder);
    }

    public record Command(UUID serviceOrderId, ServiceOrderStatus status) {
    }
}
