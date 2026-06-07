package br.com.autocarehub.application.usecase.workshopservice;

import br.com.autocarehub.application.ResourceNotFoundException;
import br.com.autocarehub.application.repository.WorkshopServiceRepository;
import br.com.autocarehub.domain.WorkshopService;
import java.util.UUID;

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
