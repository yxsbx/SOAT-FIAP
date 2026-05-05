package br.com.autocarehub.infrastructure.persistence.repository;

import br.com.autocarehub.infrastructure.persistence.entity.ServiceOrderJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceOrderJpaRepository extends JpaRepository<ServiceOrderJpaEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {"services", "parts"})
    List<ServiceOrderJpaEntity> findAll();

    @Override
    @EntityGraph(attributePaths = {"services", "parts"})
    Optional<ServiceOrderJpaEntity> findById(UUID id);

    @EntityGraph(attributePaths = {"services", "parts"})
    List<ServiceOrderJpaEntity> findByCustomerId(UUID customerId);
}
