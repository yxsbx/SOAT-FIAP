package br.com.autocarehub.application.usecase.serviceorder;

import br.com.autocarehub.application.ApplicationException;
import br.com.autocarehub.application.ResourceNotFoundException;
import br.com.autocarehub.application.repository.CustomerRepository;
import br.com.autocarehub.application.repository.ServiceOrderRepository;
import br.com.autocarehub.application.repository.VehicleRepository;
import br.com.autocarehub.domain.Customer;
import br.com.autocarehub.domain.Document;
import br.com.autocarehub.domain.ServiceOrder;
import br.com.autocarehub.domain.Vehicle;
import java.util.UUID;

public class CreateServiceOrderUseCase {

  private final ServiceOrderRepository serviceOrderRepository;
  private final CustomerRepository customerRepository;
  private final VehicleRepository vehicleRepository;

  public CreateServiceOrderUseCase(
      ServiceOrderRepository serviceOrderRepository,
      CustomerRepository customerRepository,
      VehicleRepository vehicleRepository) {
    this.serviceOrderRepository = serviceOrderRepository;
    this.customerRepository = customerRepository;
    this.vehicleRepository = vehicleRepository;
  }

  public ServiceOrder execute(Command command) {
    Customer customer =
        customerRepository
            .findByDocument(Document.from(command.customerDocument()))
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    Vehicle vehicle =
        vehicleRepository
            .findById(command.vehicleId())
            .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
    if (!vehicle.customerId().equals(customer.id())) {
      throw new ApplicationException("Vehicle does not belong to customer");
    }
    ServiceOrder serviceOrder =
        new ServiceOrder(customer.id(), command.vehicleId(), command.diagnosticNotes());
    return serviceOrderRepository.save(serviceOrder);
  }

  public record Command(String customerDocument, UUID vehicleId, String diagnosticNotes) {}
}
