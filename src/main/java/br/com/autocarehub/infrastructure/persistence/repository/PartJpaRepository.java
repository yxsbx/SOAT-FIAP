package br.com.autocarehub.infrastructure.persistence.repository;

import br.com.autocarehub.infrastructure.persistence.entity.PartJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PartJpaRepository extends JpaRepository<PartJpaEntity, UUID> {
}
