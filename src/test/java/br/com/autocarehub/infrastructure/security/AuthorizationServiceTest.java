package br.com.autocarehub.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.autocarehub.application.repository.CustomerRepository;
import br.com.autocarehub.application.repository.ServiceOrderRepository;
import br.com.autocarehub.domain.Address;
import br.com.autocarehub.domain.Customer;
import br.com.autocarehub.domain.Document;
import br.com.autocarehub.domain.ServiceOrder;
import br.com.autocarehub.domain.User;
import br.com.autocarehub.domain.UserRole;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class AuthorizationServiceTest {

  private final InMemoryCustomerRepository customerRepository = new InMemoryCustomerRepository();
  private final InMemoryServiceOrderRepository serviceOrderRepository =
      new InMemoryServiceOrderRepository();

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldAllowCustomerToTrackOnlyOwnDocument() {
    Customer customer =
        customerRepository.save(
            new Customer(
                "Maria Silva",
                Document.from("52998224725"),
                "11999999999",
                "maria@example.com",
                address()));
    authenticateCustomer(customer.id());
    AuthorizationService authorizationService =
        new AuthorizationService(customerRepository, serviceOrderRepository);

    assertThat(authorizationService.canTrackServiceOrders(null, "529.982.247-25")).isTrue();
    assertThat(authorizationService.canTrackServiceOrders(null, "153.509.460-56")).isFalse();
  }

  @Test
  void shouldRejectTrackingWhenCustomerDocumentIsInvalid() {
    Customer customer =
        customerRepository.save(
            new Customer(
                "Maria Silva",
                Document.from("52998224725"),
                "11999999999",
                "maria@example.com",
                address()));
    authenticateCustomer(customer.id());
    AuthorizationService authorizationService =
        new AuthorizationService(customerRepository, serviceOrderRepository);

    assertThat(authorizationService.canTrackServiceOrders(null, "000")).isFalse();
  }

  private void authenticateCustomer(UUID customerId) {
    AuthenticatedUser user =
        new AuthenticatedUser(
            new User(
                UUID.randomUUID(),
                "cliente@autocarehub.com",
                "$2a$10$hashhashhashhashhashhashhashhashhashhashhashhashhash",
                UserRole.CUSTOMER,
                customerId,
                true,
                LocalDateTime.now()));
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
  }

  private static Address address() {
    return new Address(
        "Avenida Paulista", "1000", null, "Bela Vista", "Sao Paulo", "SP", "01310-100");
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

  private static class InMemoryServiceOrderRepository implements ServiceOrderRepository {

    @Override
    public ServiceOrder save(ServiceOrder serviceOrder) {
      return serviceOrder;
    }

    @Override
    public Optional<ServiceOrder> findById(UUID id) {
      return Optional.empty();
    }

    @Override
    public List<ServiceOrder> findAll() {
      return List.of();
    }

    @Override
    public List<ServiceOrder> findByCustomerId(UUID customerId) {
      return List.of();
    }

    @Override
    public List<ServiceOrder> findCompletedWithExecutionTime() {
      return List.of();
    }
  }
}
