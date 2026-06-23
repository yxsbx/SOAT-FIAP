package br.com.autocarehub.application.usecase.vehicle;

import br.com.autocarehub.application.exception.ResourceNotFoundException;
import br.com.autocarehub.application.port.out.VehicleRepository;
import br.com.autocarehub.domain.model.Vehicle;
import br.com.autocarehub.domain.valueobject.Plate;
import java.util.UUID;

public class UpdateVehicleUseCase {

    private final VehicleRepository vehicleRepository;

    public UpdateVehicleUseCase(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Vehicle execute(Command command) {
        Vehicle vehicle = vehicleRepository
                .findById(command.vehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        vehicle.update(new Plate(command.plate()), command.brand(), command.model(), command.year(), command.mileage());
        if (command.active()) {
            vehicle.activate();
        } else {
            vehicle.deactivate();
        }
        return vehicleRepository.save(vehicle);
    }

    public record Command(
            UUID vehicleId, String plate, String brand, String model, int year, int mileage, boolean active) {}
}
