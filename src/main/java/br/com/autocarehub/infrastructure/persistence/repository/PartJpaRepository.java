package br.com.autocarehub.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.autocarehub.infrastructure.persistence.entity.PartJpaEntity;

public interface PartJpaRepository extends JpaRepository<PartJpaEntity, UUID> {
}
