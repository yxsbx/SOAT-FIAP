package br.com.autocarehub.infrastructure.persistence.adapter;

import br.com.autocarehub.application.repository.WorkshopServiceRepository;
import br.com.autocarehub.domain.WorkshopService;
import br.com.autocarehub.infrastructure.persistence.mapper.WorkshopServiceJpaMapper;
import br.com.autocarehub.infrastructure.persistence.repository.WorkshopServiceJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class WorkshopServiceRepositoryAdapter implements WorkshopServiceRepository {

    private final WorkshopServiceJpaRepository workshopServiceJpaRepository;

    public WorkshopServiceRepositoryAdapter(WorkshopServiceJpaRepository workshopServiceJpaRepository) {
        this.workshopServiceJpaRepository = workshopServiceJpaRepository;
    }

    @Override
    public WorkshopService save(WorkshopService workshopService) {
        return WorkshopServiceJpaMapper.toDomain(workshopServiceJpaRepository.save(WorkshopServiceJpaMapper.toEntity(workshopService)));
    }

    @Override
    public Optional<WorkshopService> findById(UUID id) {
        return workshopServiceJpaRepository.findById(id).map(WorkshopServiceJpaMapper::toDomain);
    }

    @Override
    public List<WorkshopService> findAll() {
        return workshopServiceJpaRepository.findAll().stream().map(WorkshopServiceJpaMapper::toDomain).toList();
    }
}
