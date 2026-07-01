package br.com.autocarehub.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.autocarehub.infrastructure.persistence.entity.VehicleJpaEntity;

public interface VehicleJpaRepository extends JpaRepository<VehicleJpaEntity, UUID> {

    List<VehicleJpaEntity> findByCustomerId(UUID customerId);
}
