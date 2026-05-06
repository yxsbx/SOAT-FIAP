package br.com.autocarehub.application.repository;

import br.com.autocarehub.domain.Customer;
import br.com.autocarehub.domain.Document;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

  Customer save(Customer customer);

  Optional<Customer> findById(UUID id);

  Optional<Customer> findByDocument(Document document);

  List<Customer> findAll();
}
