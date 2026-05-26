package br.com.autocarehub.application.usecase.serviceorder;

import br.com.autocarehub.application.ApplicationException;
import br.com.autocarehub.application.ResourceNotFoundException;
import br.com.autocarehub.application.repository.CustomerRepository;
import br.com.autocarehub.application.repository.PartRepository;
import br.com.autocarehub.application.repository.ServiceOrderRepository;
import br.com.autocarehub.application.repository.VehicleRepository;
import br.com.autocarehub.application.repository.WorkshopServiceRepository;
import br.com.autocarehub.domain.Address;
import br.com.autocarehub.domain.Customer;
import br.com.autocarehub.domain.Document;
import br.com.autocarehub.domain.DomainException;
import br.com.autocarehub.domain.Part;
import br.com.autocarehub.domain.Plate;
import br.com.autocarehub.domain.ServiceOrder;
import br.com.autocarehub.domain.Vehicle;
import br.com.autocarehub.domain.WorkshopService;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CreateServiceOrderUseCase {

  private final ServiceOrderRepository serviceOrderRepository;
  private final CustomerRepository customerRepository;
  private final VehicleRepository vehicleRepository;
  private final WorkshopServiceRepository workshopServiceRepository;
  private final PartRepository partRepository;

  public CreateServiceOrderUseCase(
      ServiceOrderRepository serviceOrderRepository,
      CustomerRepository customerRepository,
      VehicleRepository vehicleRepository,
      WorkshopServiceRepository workshopServiceRepository,
      PartRepository partRepository) {
    this.serviceOrderRepository = serviceOrderRepository;
    this.customerRepository = customerRepository;
    this.vehicleRepository = vehicleRepository;
    this.workshopServiceRepository = workshopServiceRepository;
    this.partRepository = partRepository;
  }

  public ServiceOrder execute(Command command) {
    Customer customer = findOrCreateCustomer(command);
    Vehicle vehicle = findOrCreateVehicle(command, customer.id());
    if (!vehicle.customerId().equals(customer.id())) {
      throw new ApplicationException("Vehicle does not belong to customer");
    }

    if (command.services() == null || command.services().isEmpty()) {
      throw new ApplicationException("Service order must have at least one requested service");
    }

    ServiceOrder serviceOrder =
        new ServiceOrder(customer.id(), vehicle.id(), command.diagnosticNotes());
    addRequestedServices(serviceOrder, command.services());
    addRequestedParts(serviceOrder, command.parts());
    if (command.generateBudget()) {
      serviceOrder.generateBudget();
    }
    return serviceOrderRepository.save(serviceOrder);
  }

  private Customer findOrCreateCustomer(Command command) {
    Document document = Document.from(command.customerDocument());
    return customerRepository
        .findByDocument(document)
        .orElseGet(
            () -> {
              CustomerInput customer = command.customer();
              if (customer == null) {
                throw new ResourceNotFoundException(
                    "Customer not found and customer data was not provided");
              }
              Customer newCustomer =
                  new Customer(
                      customer.name(),
                      document,
                      customer.phone(),
                      customer.email(),
                      customer.address());
              return customerRepository.save(newCustomer);
            });
  }

  private Vehicle findOrCreateVehicle(Command command, UUID customerId) {
    if (command.vehicleId() != null) {
      return vehicleRepository
          .findById(command.vehicleId())
          .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
    }

    VehicleInput vehicle = command.vehicle();
    if (vehicle == null) {
      throw new ResourceNotFoundException("Vehicle not found and vehicle data was not provided");
    }

    Plate plate = new Plate(vehicle.plate());
    return vehicleRepository.findByCustomerId(customerId).stream()
        .filter(existingVehicle -> existingVehicle.plate().equals(plate))
        .findFirst()
        .orElseGet(
            () ->
                vehicleRepository.save(
                    new Vehicle(
                        customerId,
                        plate,
                        vehicle.brand(),
                        vehicle.model(),
                        vehicle.year(),
                        vehicle.mileage())));
  }

  private void addRequestedServices(ServiceOrder serviceOrder, List<ServiceInput> services) {
    services.forEach(
        serviceInput -> {
          WorkshopService service =
              workshopServiceRepository
                  .findById(serviceInput.serviceId())
                  .orElseThrow(() -> new ResourceNotFoundException("Workshop service not found"));
          if (!service.active()) {
            throw new DomainException("Workshop service is inactive");
          }
          serviceOrder.addService(service, serviceInput.quantity());
        });
  }

  private void addRequestedParts(ServiceOrder serviceOrder, List<PartInput> parts) {
    if (parts == null) {
      return;
    }
    parts.forEach(
        partInput -> {
          Part part =
              partRepository
                  .findById(partInput.partId())
                  .orElseThrow(() -> new ResourceNotFoundException("Part not found"));
          if (!part.active()) {
            throw new DomainException("Part is inactive");
          }
          serviceOrder.addPart(part, partInput.quantity());
        });
  }

  public record Command(
      String customerDocument,
      CustomerInput customer,
      UUID vehicleId,
      VehicleInput vehicle,
      String diagnosticNotes,
      List<ServiceInput> services,
      List<PartInput> parts,
      boolean generateBudget) {

    public Command {
      Objects.requireNonNull(customerDocument, "customerDocument is required");
      Objects.requireNonNull(diagnosticNotes, "diagnosticNotes is required");
    }
  }

  public record CustomerInput(String name, String phone, String email, Address address) {}

  public record VehicleInput(String plate, String brand, String model, int year, int mileage) {}

  public record ServiceInput(UUID serviceId, int quantity) {}

  public record PartInput(UUID partId, int quantity) {}
}
