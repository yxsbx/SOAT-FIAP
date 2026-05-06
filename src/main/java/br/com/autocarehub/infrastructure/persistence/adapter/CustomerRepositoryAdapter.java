package br.com.autocarehub.infrastructure.persistence.adapter;

import br.com.autocarehub.application.repository.CustomerRepository;
import br.com.autocarehub.domain.Customer;
import br.com.autocarehub.domain.Document;
import br.com.autocarehub.infrastructure.persistence.mapper.CustomerJpaMapper;
import br.com.autocarehub.infrastructure.persistence.repository.CustomerJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepositoryAdapter implements CustomerRepository {

  private final CustomerJpaRepository customerJpaRepository;

  public CustomerRepositoryAdapter(CustomerJpaRepository customerJpaRepository) {
    this.customerJpaRepository = customerJpaRepository;
  }

  @Override
  public Customer save(Customer customer) {
    return CustomerJpaMapper.toDomain(
        customerJpaRepository.save(CustomerJpaMapper.toEntity(customer)));
  }

  @Override
  public Optional<Customer> findById(UUID id) {
    return customerJpaRepository.findById(id).map(CustomerJpaMapper::toDomain);
  }

  @Override
  public Optional<Customer> findByDocument(Document document) {
    return customerJpaRepository
        .findByDocumentValue(document.value())
        .map(CustomerJpaMapper::toDomain);
  }

  @Override
  public List<Customer> findAll() {
    return customerJpaRepository.findAll().stream().map(CustomerJpaMapper::toDomain).toList();
  }
}
