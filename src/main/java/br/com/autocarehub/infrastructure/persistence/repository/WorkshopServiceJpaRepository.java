package br.com.autocarehub.infrastructure.persistence.repository;

import br.com.autocarehub.infrastructure.persistence.entity.WorkshopServiceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkshopServiceJpaRepository
        extends JpaRepository<WorkshopServiceJpaEntity, UUID> {
}
