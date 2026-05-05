package br.com.autocarehub.interfaces.rest.impl;

import br.com.autocarehub.interfaces.rest.generated.api.WorkshopServicesApi;
import br.com.autocarehub.interfaces.rest.generated.model.CreateWorkshopServiceRequest;
import br.com.autocarehub.interfaces.rest.generated.model.UpdateWorkshopServiceRequest;
import br.com.autocarehub.interfaces.rest.generated.model.WorkshopServiceListResponse;
import br.com.autocarehub.interfaces.rest.generated.model.WorkshopServiceResponse;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkshopServicesController implements WorkshopServicesApi {

    @Override
    public ResponseEntity<WorkshopServiceResponse> createWorkshopService(CreateWorkshopServiceRequest createWorkshopServiceRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<Void> deleteWorkshopService(UUID serviceId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<WorkshopServiceResponse> getWorkshopServiceById(UUID serviceId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<WorkshopServiceListResponse> listWorkshopServices(Integer page, Integer size, Boolean active) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<WorkshopServiceResponse> updateWorkshopService(UUID serviceId, UpdateWorkshopServiceRequest updateWorkshopServiceRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
