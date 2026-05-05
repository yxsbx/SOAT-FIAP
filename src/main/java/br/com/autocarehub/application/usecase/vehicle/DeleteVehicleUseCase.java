package br.com.autocarehub.application.usecase.vehicle;

import br.com.autocarehub.application.ResourceNotFoundException;
import br.com.autocarehub.application.repository.VehicleRepository;
import br.com.autocarehub.domain.Vehicle;
import java.util.UUID;

public class DeleteVehicleUseCase {

    private final VehicleRepository vehicleRepository;

    public DeleteVehicleUseCase(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public void execute(UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        vehicle.deactivate();
        vehicleRepository.save(vehicle);
    }
}
