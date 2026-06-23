package br.com.autocarehub.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.autocarehub.domain.model.Vehicle;

public interface VehicleRepository {

    Vehicle save(Vehicle vehicle);

    Optional<Vehicle> findById(UUID id);

    List<Vehicle> findAll();

    List<Vehicle> findByCustomerId(UUID customerId);
}
