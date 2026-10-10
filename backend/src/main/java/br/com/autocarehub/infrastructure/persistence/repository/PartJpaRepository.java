package br.com.autocarehub.infrastructure.persistence.repository;

import br.com.autocarehub.infrastructure.persistence.entity.PartJpaEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface PartJpaRepository extends JpaRepository<PartJpaEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select entity from PartJpaEntity entity where entity.id = :id")
    Optional<PartJpaEntity> findByIdForUpdate(UUID id);
}
