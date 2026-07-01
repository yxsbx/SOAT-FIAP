package br.com.autocarehub.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.autocarehub.infrastructure.persistence.entity.WorkshopServiceJpaEntity;

public interface WorkshopServiceJpaRepository extends JpaRepository<WorkshopServiceJpaEntity, UUID> {
}
