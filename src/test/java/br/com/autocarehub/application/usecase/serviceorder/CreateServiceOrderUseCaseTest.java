package br.com.autocarehub.application.usecase.serviceorder;

import br.com.autocarehub.application.ApplicationException;
import br.com.autocarehub.application.repository.CustomerRepository;
import br.com.autocarehub.application.repository.PartRepository;
import br.com.autocarehub.application.repository.ServiceOrderRepository;
import br.com.autocarehub.application.repository.VehicleRepository;
import br.com.autocarehub.application.repository.WorkshopServiceRepository;
import br.com.autocarehub.domain.Address;
import br.com.autocarehub.domain.Customer;
import br.com.autocarehub.domain.Document;
import br.com.autocarehub.domain.Money;
import br.com.autocarehub.domain.Part;
import br.com.autocarehub.domain.ServiceOrder;
import br.com.autocarehub.domain.ServiceOrderStatus;
import br.com.autocarehub.domain.Vehicle;
import br.com.autocarehub.domain.WorkshopService;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateServiceOrderUseCaseTest {

    private final InMemoryCustomerRepository customerRepository = new InMemoryCustomerRepository();
    private final InMemoryVehicleRepository vehicleRepository = new InMemoryVehicleRepository();
    private final InMemoryServiceOrderRepository serviceOrderRepository =
            new InMemoryServiceOrderRepository();
    private final InMemoryWorkshopServiceRepository workshopServiceRepository =
            new InMemoryWorkshopServiceRepository();
    private final InMemoryPartRepository partRepository = new InMemoryPartRepository();

    @Test
    void shouldCreateCustomerVehicleServiceOrderAndBudgetInSingleFlow() {
        WorkshopService service =
                workshopServiceRepository.save(
                        new WorkshopService(
                                "Troca de oleo",
                                "Substituicao de oleo do motor",
                                Money.of("120.00"),
                                60));
        Part part =
                partRepository.save(
                        new Part(
                                "Filtro de oleo",
                                "FILTRO-001",
                                "Filtros",
                                "Oleo",
                                "Bosch",
                                Money.of("40.00"),
                                10,
                                2));
        CreateServiceOrderUseCase useCase = useCase();

        ServiceOrder serviceOrder =
                useCase.execute(
                        new CreateServiceOrderUseCase.Command(
                                "52998224725",
                                new CreateServiceOrderUseCase.CustomerInput(
                                        "Maria Silva",
                                        "11999999999",
                                        "maria@example.com",
                                        address()),
                                null,
                                new CreateServiceOrderUseCase.VehicleInput(
                                        "ABC1D23", "Honda", "Civic", 2020, 35000),
                                "Cliente relata barulho no motor",
                                List.of(new CreateServiceOrderUseCase.ServiceInput(service.id(), 2)),
                                List.of(new CreateServiceOrderUseCase.PartInput(part.id(), 1)),
                                true));

        assertThat(customerRepository.findByDocument(Document.from("52998224725"))).isPresent();
        assertThat(vehicleRepository.findByCustomerId(serviceOrder.customerId())).hasSize(1);
        assertThat(serviceOrder.status()).isEqualTo(ServiceOrderStatus.AGUARDANDO_APROVACAO);
        assertThat(serviceOrder.servicesTotal().value()).isEqualByComparingTo("240.00");
        assertThat(serviceOrder.partsTotal().value()).isEqualByComparingTo("40.00");
        assertThat(serviceOrder.totalAmount().value()).isEqualByComparingTo("280.00");
        assertThat(serviceOrder.budgetGeneratedAt()).isNotNull();
    }

    @Test
    void shouldRejectServiceOrderWithoutRequestedServices() {
        CreateServiceOrderUseCase useCase = useCase();

        assertThatThrownBy(
                        () ->
                                useCase.execute(
                                        new CreateServiceOrderUseCase.Command(
                                                "52998224725",
                                                new CreateServiceOrderUseCase.CustomerInput(
                                                        "Maria Silva",
                                                        "11999999999",
                                                        "maria@example.com",
                                                        address()),
                                                null,
                                                new CreateServiceOrderUseCase.VehicleInput(
                                                        "ABC1D23", "Honda", "Civic", 2020, 35000),
                                                "Cliente relata barulho no motor",
                                                List.of(),
                                                List.of(),
                                                true)))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("Service order must have at least one requested service");
    }

    private CreateServiceOrderUseCase useCase() {
        return new CreateServiceOrderUseCase(
                serviceOrderRepository,
                customerRepository,
                vehicleRepository,
                workshopServiceRepository,
                partRepository);
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

    private static class InMemoryVehicleRepository implements VehicleRepository {

        private final Map<UUID, Vehicle> vehicles = new LinkedHashMap<>();

        @Override
        public Vehicle save(Vehicle vehicle) {
            vehicles.put(vehicle.id(), vehicle);
            return vehicle;
        }

        @Override
        public Optional<Vehicle> findById(UUID id) {
            return Optional.ofNullable(vehicles.get(id));
        }

        @Override
        public List<Vehicle> findAll() {
            return List.copyOf(vehicles.values());
        }

        @Override
        public List<Vehicle> findByCustomerId(UUID customerId) {
            return vehicles.values().stream()
                    .filter(vehicle -> vehicle.customerId().equals(customerId))
                    .toList();
        }
    }

    private static class InMemoryServiceOrderRepository implements ServiceOrderRepository {

        private final Map<UUID, ServiceOrder> serviceOrders = new LinkedHashMap<>();

        @Override
        public ServiceOrder save(ServiceOrder serviceOrder) {
            serviceOrders.put(serviceOrder.id(), serviceOrder);
            return serviceOrder;
        }

        @Override
        public Optional<ServiceOrder> findById(UUID id) {
            return Optional.ofNullable(serviceOrders.get(id));
        }

        @Override
        public List<ServiceOrder> findAll() {
            return List.copyOf(serviceOrders.values());
        }

        @Override
        public List<ServiceOrder> findByCustomerId(UUID customerId) {
            return serviceOrders.values().stream()
                    .filter(serviceOrder -> serviceOrder.customerId().equals(customerId))
                    .toList();
        }

        @Override
        public List<ServiceOrder> findCompletedWithExecutionTime() {
            return List.of();
        }
    }

    private static class InMemoryWorkshopServiceRepository implements WorkshopServiceRepository {

        private final Map<UUID, WorkshopService> services = new LinkedHashMap<>();

        @Override
        public WorkshopService save(WorkshopService workshopService) {
            services.put(workshopService.id(), workshopService);
            return workshopService;
        }

        @Override
        public Optional<WorkshopService> findById(UUID id) {
            return Optional.ofNullable(services.get(id));
        }

        @Override
        public List<WorkshopService> findAll() {
            return List.copyOf(services.values());
        }
    }

    private static class InMemoryPartRepository implements PartRepository {

        private final Map<UUID, Part> parts = new LinkedHashMap<>();

        @Override
        public Part save(Part part) {
            parts.put(part.id(), part);
            return part;
        }

        @Override
        public Optional<Part> findById(UUID id) {
            return Optional.ofNullable(parts.get(id));
        }

        @Override
        public List<Part> findAll() {
            return List.copyOf(parts.values());
        }
    }
}
