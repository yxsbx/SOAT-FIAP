package br.com.autocarehub.application.usecase.workshopservice;

import br.com.autocarehub.application.repository.WorkshopServiceRepository;
import br.com.autocarehub.domain.WorkshopService;
import java.util.List;

public class ListWorkshopServicesUseCase {

    private final WorkshopServiceRepository workshopServiceRepository;

    public ListWorkshopServicesUseCase(WorkshopServiceRepository workshopServiceRepository) {
        this.workshopServiceRepository = workshopServiceRepository;
    }

    public List<WorkshopService> execute() {
        return workshopServiceRepository.findAll();
    }
}
