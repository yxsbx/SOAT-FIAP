package br.com.autocarehub.application.usecase.serviceorder;

import br.com.autocarehub.application.ResourceNotFoundException;
import br.com.autocarehub.application.repository.ServiceOrderRepository;
import br.com.autocarehub.domain.InvalidServiceOrderStatusTransitionException;
import br.com.autocarehub.domain.ServiceOrder;
import br.com.autocarehub.domain.ServiceOrderStatus;

import java.util.UUID;

public class UpdateServiceOrderStatusUseCase {

    private final ServiceOrderRepository serviceOrderRepository;

    public UpdateServiceOrderStatusUseCase(ServiceOrderRepository serviceOrderRepository) {
        this.serviceOrderRepository = serviceOrderRepository;
    }

    public ServiceOrder execute(Command command) {
        ServiceOrder serviceOrder =
                serviceOrderRepository
                        .findById(command.serviceOrderId())
                        .orElseThrow(() -> new ResourceNotFoundException("Service order not found"));
        switch (command.status()) {
            case EM_DIAGNOSTICO -> serviceOrder.startDiagnosis();
            case AGUARDANDO_APROVACAO -> serviceOrder.generateBudget();
            case EM_EXECUCAO -> serviceOrder.startExecution();
            case FINALIZADA -> serviceOrder.finish();
            case ENTREGUE -> serviceOrder.deliver();
            case RECEBIDA -> throw new InvalidServiceOrderStatusTransitionException(
                    "Service order cannot return to received status");
        }
        return serviceOrderRepository.save(serviceOrder);
    }

    public record Command(UUID serviceOrderId, ServiceOrderStatus status) {
    }
}
