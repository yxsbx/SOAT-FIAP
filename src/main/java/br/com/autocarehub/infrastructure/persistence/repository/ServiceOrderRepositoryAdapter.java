package br.com.autocarehub.infrastructure.persistence.repository;

import br.com.autocarehub.application.port.out.ServiceOrderRepository;
import br.com.autocarehub.domain.model.ServiceOrder;
import br.com.autocarehub.infrastructure.persistence.mapper.ServiceOrderJpaMapper;
import br.com.autocarehub.infrastructure.persistence.repository.ServiceOrderJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ServiceOrderRepositoryAdapter implements ServiceOrderRepository {

  private final ServiceOrderJpaRepository serviceOrderJpaRepository;

  public ServiceOrderRepositoryAdapter(ServiceOrderJpaRepository serviceOrderJpaRepository) {
    this.serviceOrderJpaRepository = serviceOrderJpaRepository;
  }

  @Override
  public ServiceOrder save(ServiceOrder serviceOrder) {
    return ServiceOrderJpaMapper.toDomain(
        serviceOrderJpaRepository.save(ServiceOrderJpaMapper.toEntity(serviceOrder)));
  }

  @Override
  public Optional<ServiceOrder> findById(UUID id) {
    return serviceOrderJpaRepository.findById(id).map(ServiceOrderJpaMapper::toDomain);
  }

  @Override
  public List<ServiceOrder> findAll() {
    return serviceOrderJpaRepository.findAll().stream()
        .map(ServiceOrderJpaMapper::toDomain)
        .toList();
  }

  @Override
  public List<ServiceOrder> findByCustomerId(UUID customerId) {
    return serviceOrderJpaRepository.findByCustomerId(customerId).stream()
        .map(ServiceOrderJpaMapper::toDomain)
        .toList();
  }

  @Override
  public List<ServiceOrder> findCompletedWithExecutionTime() {
    return serviceOrderJpaRepository.findCompletedWithExecutionTime().stream()
        .map(ServiceOrderJpaMapper::toDomain)
        .toList();
  }
}
