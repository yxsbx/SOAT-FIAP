package br.com.autocarehub.interfaces.rest.impl;

import br.com.autocarehub.interfaces.rest.generated.api.VehiclesApi;
import br.com.autocarehub.interfaces.rest.generated.model.CreateVehicleRequest;
import br.com.autocarehub.interfaces.rest.generated.model.UpdateVehicleRequest;
import br.com.autocarehub.interfaces.rest.generated.model.VehicleListResponse;
import br.com.autocarehub.interfaces.rest.generated.model.VehicleResponse;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VehiclesController implements VehiclesApi {

    @Override
    public ResponseEntity<VehicleResponse> createVehicle(CreateVehicleRequest createVehicleRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<Void> deleteVehicle(UUID vehicleId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<VehicleResponse> getVehicleById(UUID vehicleId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<VehicleListResponse> listVehicles(Integer page, Integer size, Boolean active) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<VehicleListResponse> listVehiclesByCustomer(UUID customerId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<VehicleResponse> updateVehicle(UUID vehicleId, UpdateVehicleRequest updateVehicleRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
