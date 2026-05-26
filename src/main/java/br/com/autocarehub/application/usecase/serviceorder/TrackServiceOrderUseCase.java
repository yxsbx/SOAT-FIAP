package br.com.autocarehub.application.usecase.serviceorder;

import br.com.autocarehub.application.ApplicationException;
import br.com.autocarehub.application.ResourceNotFoundException;
import br.com.autocarehub.application.repository.CustomerRepository;
import br.com.autocarehub.application.repository.ServiceOrderRepository;
import br.com.autocarehub.application.repository.VehicleRepository;
import br.com.autocarehub.domain.Customer;
import br.com.autocarehub.domain.Document;
import br.com.autocarehub.domain.Plate;
import br.com.autocarehub.domain.ServiceOrder;
import br.com.autocarehub.domain.Vehicle;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class TrackServiceOrderUseCase {

  private final ServiceOrderRepository serviceOrderRepository;
  private final CustomerRepository customerRepository;
  private final VehicleRepository vehicleRepository;

  public TrackServiceOrderUseCase(
      ServiceOrderRepository serviceOrderRepository,
      CustomerRepository customerRepository,
      VehicleRepository vehicleRepository) {
    this.serviceOrderRepository = serviceOrderRepository;
    this.customerRepository = customerRepository;
    this.vehicleRepository = vehicleRepository;
  }

  public List<Output> execute(Query query) {
    if (query.serviceOrderId() != null) {
      ServiceOrder serviceOrder =
          serviceOrderRepository
              .findById(query.serviceOrderId())
              .orElseThrow(() -> new ResourceNotFoundException("Service order not found"));
      Customer customer = findCustomer(serviceOrder.customerId());
      Vehicle vehicle = findVehicle(serviceOrder.vehicleId());
      ensureFiltersMatch(query, customer, vehicle);
      return List.of(new Output(serviceOrder, customer, vehicle));
    }

    if (isBlank(query.customerDocument()) || isBlank(query.plate())) {
      throw new ApplicationException(
          "Provide serviceOrderId or both customerDocument and plate to track a service order");
    }

    Customer customer =
        customerRepository
            .findByDocument(Document.from(query.customerDocument()))
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    Plate plate = new Plate(query.plate());
    Vehicle vehicle =
        vehicleRepository.findByCustomerId(customer.id()).stream()
            .filter(candidate -> candidate.plate().equals(plate))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

    return serviceOrderRepository.findByCustomerId(customer.id()).stream()
        .filter(serviceOrder -> serviceOrder.vehicleId().equals(vehicle.id()))
        .sorted(Comparator.comparing(ServiceOrder::createdAt).reversed())
        .map(serviceOrder -> new Output(serviceOrder, customer, vehicle))
        .toList();
  }

  private Customer findCustomer(UUID customerId) {
    return customerRepository
        .findById(customerId)
        .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
  }

  private Vehicle findVehicle(UUID vehicleId) {
    return vehicleRepository
        .findById(vehicleId)
        .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
  }

  private void ensureFiltersMatch(Query query, Customer customer, Vehicle vehicle) {
    if (!isBlank(query.customerDocument())
        && !customer.document().equals(Document.from(query.customerDocument()))) {
      throw new ResourceNotFoundException("Service order not found for customer document");
    }
    if (!isBlank(query.plate()) && !vehicle.plate().equals(new Plate(query.plate()))) {
      throw new ResourceNotFoundException("Service order not found for vehicle plate");
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  public record Query(UUID serviceOrderId, String customerDocument, String plate) {}

  public record Output(ServiceOrder serviceOrder, Customer customer, Vehicle vehicle) {}
}
