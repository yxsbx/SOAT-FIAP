package br.com.autocarehub.application.usecase.vehicle;

import br.com.autocarehub.application.ResourceNotFoundException;
import br.com.autocarehub.application.repository.CustomerRepository;
import br.com.autocarehub.application.repository.VehicleRepository;
import br.com.autocarehub.domain.Plate;
import br.com.autocarehub.domain.Vehicle;
import java.util.UUID;

public class CreateVehicleUseCase {

  private final VehicleRepository vehicleRepository;
  private final CustomerRepository customerRepository;

  public CreateVehicleUseCase(
      VehicleRepository vehicleRepository, CustomerRepository customerRepository) {
    this.vehicleRepository = vehicleRepository;
    this.customerRepository = customerRepository;
  }

  public Vehicle execute(Command command) {
    customerRepository
        .findById(command.customerId())
        .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    Vehicle vehicle =
        new Vehicle(
            command.customerId(),
            new Plate(command.plate()),
            command.brand(),
            command.model(),
            command.year(),
            command.mileage());
    return vehicleRepository.save(vehicle);
  }

  public record Command(
      UUID customerId, String plate, String brand, String model, int year, int mileage) {}
}
