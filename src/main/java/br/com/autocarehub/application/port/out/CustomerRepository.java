package br.com.autocarehub.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.autocarehub.domain.model.Customer;
import br.com.autocarehub.domain.valueobject.Document;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(UUID id);

    Optional<Customer> findByDocument(Document document);

    List<Customer> findAll();
}
