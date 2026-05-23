package br.com.autocarehub.application.usecase.serviceorder;

import br.com.autocarehub.application.ResourceNotFoundException;
import br.com.autocarehub.application.repository.PartRepository;
import br.com.autocarehub.application.repository.ServiceOrderRepository;
import br.com.autocarehub.domain.Part;
import br.com.autocarehub.domain.ServiceOrder;

import java.util.UUID;

public class AddPartToServiceOrderUseCase {

    private final ServiceOrderRepository serviceOrderRepository;
    private final PartRepository partRepository;

    public AddPartToServiceOrderUseCase(
            ServiceOrderRepository serviceOrderRepository, PartRepository partRepository) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.partRepository = partRepository;
    }

    public ServiceOrder execute(Command command) {
        ServiceOrder serviceOrder =
                serviceOrderRepository
                        .findById(command.serviceOrderId())
                        .orElseThrow(() -> new ResourceNotFoundException("Service order not found"));
        Part part =
                partRepository
                        .findById(command.partId())
                        .orElseThrow(() -> new ResourceNotFoundException("Part not found"));
        serviceOrder.addPart(part, command.quantity());
        part.reduceStock(command.quantity());
        partRepository.save(part);
        return serviceOrderRepository.save(serviceOrder);
    }

    public record Command(UUID serviceOrderId, UUID partId, int quantity) {
    }
}
