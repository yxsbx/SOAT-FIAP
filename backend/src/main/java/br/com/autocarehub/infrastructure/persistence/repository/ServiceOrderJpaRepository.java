package br.com.autocarehub.infrastructure.persistence.repository;

import br.com.autocarehub.infrastructure.persistence.entity.ServiceOrderJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceOrderJpaRepository extends JpaRepository<ServiceOrderJpaEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {"services", "parts"})
    @NonNull
    List<ServiceOrderJpaEntity> findAll();

    @Override
    @EntityGraph(attributePaths = {"services", "parts"})
    @NonNull
    Optional<ServiceOrderJpaEntity> findById(@NonNull UUID id);

    @EntityGraph(attributePaths = {"services", "parts"})
    List<ServiceOrderJpaEntity> findByCustomerId(UUID customerId);

    @EntityGraph(attributePaths = {"services", "parts"})
    @Query(
            """
                    select serviceOrder
                    from ServiceOrderJpaEntity serviceOrder
                    where serviceOrder.startedAt is not null
                      and serviceOrder.finishedAt is not null
                    """)
    List<ServiceOrderJpaEntity> findCompletedWithExecutionTime();

    @EntityGraph(attributePaths = {"services", "parts"})
    @Query(
            """
                    select serviceOrder
                    from ServiceOrderJpaEntity serviceOrder
                    where serviceOrder.status not in ('FINISHED', 'DELIVERED')
                      and (:status is null or serviceOrder.status = :status)
                      and (:customerId is null or serviceOrder.customerId = :customerId)
                      and (:vehicleId is null or serviceOrder.vehicleId = :vehicleId)
                      and (:createdFrom is null or serviceOrder.createdAt >= :createdFrom)
                      and (:createdTo is null or serviceOrder.createdAt <= :createdTo)
                    order by
                      case serviceOrder.status
                        when 'IN_PROGRESS' then 0
                        when 'WAITING_APPROVAL' then 1
                        when 'IN_DIAGNOSIS' then 2
                        when 'RECEIVED' then 3
                        else 4
                      end,
                      serviceOrder.createdAt asc
                    """)
    List<ServiceOrderJpaEntity> findOperationalQueue(
            @Param("status") String status,
            @Param("customerId") UUID customerId,
            @Param("vehicleId") UUID vehicleId,
            @Param("createdFrom") java.time.LocalDateTime createdFrom,
            @Param("createdTo") java.time.LocalDateTime createdTo,
            Pageable pageable);
}
