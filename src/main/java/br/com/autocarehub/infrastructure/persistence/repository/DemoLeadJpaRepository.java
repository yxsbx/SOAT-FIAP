package br.com.autocarehub.infrastructure.persistence.repository;

import br.com.autocarehub.infrastructure.persistence.entity.DemoLeadJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DemoLeadJpaRepository extends JpaRepository<DemoLeadJpaEntity, UUID> {
}
