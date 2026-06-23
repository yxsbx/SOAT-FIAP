package br.com.autocarehub.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.autocarehub.infrastructure.persistence.entity.DemoLeadJpaEntity;

public interface DemoLeadJpaRepository extends JpaRepository<DemoLeadJpaEntity, UUID> {

}
