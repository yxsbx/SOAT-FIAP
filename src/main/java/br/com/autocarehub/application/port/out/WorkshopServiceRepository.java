package br.com.autocarehub.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.autocarehub.domain.model.WorkshopService;

public interface WorkshopServiceRepository {

    WorkshopService save(WorkshopService workshopService);

    Optional<WorkshopService> findById(UUID id);

    List<WorkshopService> findAll();
}
