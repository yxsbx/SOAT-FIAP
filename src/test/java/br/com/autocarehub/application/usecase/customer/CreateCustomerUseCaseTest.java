package br.com.autocarehub.application.usecase.customer;

import br.com.autocarehub.application.ApplicationException;
import br.com.autocarehub.application.repository.CustomerRepository;
import br.com.autocarehub.domain.Address;
import br.com.autocarehub.domain.Customer;
import br.com.autocarehub.domain.Document;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateCustomerUseCaseTest {

    private final InMemoryCustomerRepository repository = new InMemoryCustomerRepository();

    private static Address address() {
        return new Address(
                "Avenida Paulista", "1000", null, "Bela Vista", "Sao Paulo", "SP", "01310-100");
    }

    @Test
    void shouldCreateCustomerWhenDocumentDoesNotExist() {
        CreateCustomerUseCase useCase = new CreateCustomerUseCase(repository);

        Customer customer =
                useCase.execute(
                        new CreateCustomerUseCase.Command(
                                "Maria Silva", "52998224725", "11999999999", "maria@example.com", address()));

        assertThat(customer.id()).isNotNull();
        assertThat(repository.findByDocument(Document.from("52998224725"))).isPresent();
    }

    @Test
    void shouldRejectDuplicatedDocument() {
        CreateCustomerUseCase useCase = new CreateCustomerUseCase(repository);
        CreateCustomerUseCase.Command command =
                new CreateCustomerUseCase.Command(
                        "Maria Silva", "52998224725", "11999999999", "maria@example.com", address());
        useCase.execute(command);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("Customer document already exists");
    }

    private static class InMemoryCustomerRepository implements CustomerRepository {

        private final Map<UUID, Customer> customers = new LinkedHashMap<>();

        @Override
        public Customer save(Customer customer) {
            customers.put(customer.id(), customer);
            return customer;
        }

        @Override
        public Optional<Customer> findById(UUID id) {
            return Optional.ofNullable(customers.get(id));
        }

        @Override
        public Optional<Customer> findByDocument(Document document) {
            return customers.values().stream()
                    .filter(customer -> customer.document().equals(document))
                    .findFirst();
        }

        @Override
        public List<Customer> findAll() {
            return List.copyOf(customers.values());
        }
    }
}
