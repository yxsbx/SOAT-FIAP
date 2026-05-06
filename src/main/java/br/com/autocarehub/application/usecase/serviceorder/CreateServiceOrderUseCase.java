package br.com.autocarehub.application.usecase.serviceorder;

import br.com.autocarehub.application.ApplicationException;
import br.com.autocarehub.application.ResourceNotFoundException;
import br.com.autocarehub.application.repository.CustomerRepository;
import br.com.autocarehub.application.repository.ServiceOrderRepository;
import br.com.autocarehub.application.repository.VehicleRepository;
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
    customerRepository
        .findById(command.customerId())
        .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    Vehicle vehicle =
        vehicleRepository
            .findById(command.vehicleId())
            .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
    if (!vehicle.customerId().equals(command.customerId())) {
      throw new ApplicationException("Vehicle does not belong to customer");
    }
    ServiceOrder serviceOrder =
        new ServiceOrder(command.customerId(), command.vehicleId(), command.diagnosticNotes());
    return serviceOrderRepository.save(serviceOrder);
  }

  public record Command(UUID customerId, UUID vehicleId, String diagnosticNotes) {}
}
