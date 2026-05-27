package br.com.autocarehub.infrastructure.persistence.repository;

import br.com.autocarehub.infrastructure.persistence.entity.VehicleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VehicleJpaRepository extends JpaRepository<VehicleJpaEntity, UUID> {

    List<VehicleJpaEntity> findByCustomerId(UUID customerId);
}
