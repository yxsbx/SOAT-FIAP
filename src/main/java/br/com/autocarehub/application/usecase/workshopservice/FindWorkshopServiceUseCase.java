package br.com.autocarehub.application.usecase.workshopservice;

import java.util.UUID;

import br.com.autocarehub.application.exception.ResourceNotFoundException;
import br.com.autocarehub.application.port.out.WorkshopServiceRepository;
import br.com.autocarehub.domain.model.WorkshopService;

public class FindWorkshopServiceUseCase {

    private final WorkshopServiceRepository workshopServiceRepository;

    public FindWorkshopServiceUseCase(WorkshopServiceRepository workshopServiceRepository) {
        this.workshopServiceRepository = workshopServiceRepository;
    }

    public WorkshopService execute(UUID serviceId) {
        return workshopServiceRepository
                .findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workshop service not found"));
    }
}
