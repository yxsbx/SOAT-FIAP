package br.com.autocarehub.interfaces.rest.impl;

import br.com.autocarehub.interfaces.rest.generated.api.PartsApi;
import br.com.autocarehub.interfaces.rest.generated.model.CreatePartRequest;
import br.com.autocarehub.interfaces.rest.generated.model.PartListResponse;
import br.com.autocarehub.interfaces.rest.generated.model.PartResponse;
import br.com.autocarehub.interfaces.rest.generated.model.UpdatePartRequest;
import br.com.autocarehub.interfaces.rest.generated.model.UpdatePartStockRequest;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PartsController implements PartsApi {

    @Override
    public ResponseEntity<PartResponse> createPart(CreatePartRequest createPartRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<Void> deletePart(UUID partId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<PartResponse> getPartById(UUID partId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<PartListResponse> listParts(Integer page, Integer size, Boolean active, Boolean lowStock) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<PartResponse> updatePart(UUID partId, UpdatePartRequest updatePartRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<PartResponse> updatePartStock(UUID partId, UpdatePartStockRequest updatePartStockRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
