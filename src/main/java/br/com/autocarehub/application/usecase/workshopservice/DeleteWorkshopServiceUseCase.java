package br.com.autocarehub.application.usecase.workshopservice;

import java.util.UUID;

import br.com.autocarehub.application.exception.ResourceNotFoundException;
import br.com.autocarehub.application.port.out.WorkshopServiceRepository;
import br.com.autocarehub.domain.model.WorkshopService;

public class DeleteWorkshopServiceUseCase {

    private final WorkshopServiceRepository workshopServiceRepository;

    public DeleteWorkshopServiceUseCase(WorkshopServiceRepository workshopServiceRepository) {
        this.workshopServiceRepository = workshopServiceRepository;
    }

    public void execute(UUID serviceId) {
        WorkshopService workshopService =
                workshopServiceRepository
                        .findById(serviceId)
                        .orElseThrow(() -> new ResourceNotFoundException("Workshop service not found"));
        workshopService.deactivate();
        workshopServiceRepository.save(workshopService);
    }
}
