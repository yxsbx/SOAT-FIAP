package br.com.autocarehub.interfaces.rest.impl;

import br.com.autocarehub.application.usecase.part.CreatePartUseCase;
import br.com.autocarehub.application.usecase.part.DeletePartUseCase;
import br.com.autocarehub.application.usecase.part.FindPartUseCase;
import br.com.autocarehub.application.usecase.part.ListPartsUseCase;
import br.com.autocarehub.application.usecase.part.UpdatePartStockUseCase;
import br.com.autocarehub.application.usecase.part.UpdatePartUseCase;
import br.com.autocarehub.domain.Part;
import br.com.autocarehub.interfaces.rest.generated.api.PartsApi;
import br.com.autocarehub.interfaces.rest.generated.model.CreatePartRequest;
import br.com.autocarehub.interfaces.rest.generated.model.PartListResponse;
import br.com.autocarehub.interfaces.rest.generated.model.PartResponse;
import br.com.autocarehub.interfaces.rest.generated.model.UpdatePartRequest;
import br.com.autocarehub.interfaces.rest.generated.model.UpdatePartStockRequest;
import br.com.autocarehub.interfaces.rest.impl.mapper.PartRestMapper;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PartsController implements PartsApi {

    private final CreatePartUseCase createPartUseCase;
    private final UpdatePartUseCase updatePartUseCase;
    private final FindPartUseCase findPartUseCase;
    private final ListPartsUseCase listPartsUseCase;
    private final DeletePartUseCase deletePartUseCase;
    private final UpdatePartStockUseCase updatePartStockUseCase;

    public PartsController(CreatePartUseCase createPartUseCase, UpdatePartUseCase updatePartUseCase, FindPartUseCase findPartUseCase, ListPartsUseCase listPartsUseCase, DeletePartUseCase deletePartUseCase, UpdatePartStockUseCase updatePartStockUseCase) {
        this.createPartUseCase = createPartUseCase;
        this.updatePartUseCase = updatePartUseCase;
        this.findPartUseCase = findPartUseCase;
        this.listPartsUseCase = listPartsUseCase;
        this.deletePartUseCase = deletePartUseCase;
        this.updatePartStockUseCase = updatePartStockUseCase;
    }

    @Override
    public ResponseEntity<PartResponse> createPart(CreatePartRequest createPartRequest) {
        Part part = createPartUseCase.execute(PartRestMapper.toCommand(createPartRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(PartRestMapper.toResponse(part));
    }

    @Override
    public ResponseEntity<Void> deletePart(UUID partId) {
        deletePartUseCase.execute(partId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PartResponse> getPartById(UUID partId) {
        return ResponseEntity.ok(PartRestMapper.toResponse(findPartUseCase.execute(partId)));
    }

    @Override
    public ResponseEntity<PartListResponse> listParts(Integer page, Integer size, Boolean active, Boolean lowStock) {
        return ResponseEntity.ok(PartRestMapper.toListResponse(listPartsUseCase.execute(PartRestMapper.toQuery(active, lowStock)), page, size));
    }

    @Override
    public ResponseEntity<PartResponse> updatePart(UUID partId, UpdatePartRequest updatePartRequest) {
        Part part = updatePartUseCase.execute(PartRestMapper.toCommand(partId, updatePartRequest));
        return ResponseEntity.ok(PartRestMapper.toResponse(part));
    }

    @Override
    public ResponseEntity<PartResponse> updatePartStock(UUID partId, UpdatePartStockRequest updatePartStockRequest) {
        Part part = updatePartStockUseCase.execute(PartRestMapper.toCommand(partId, updatePartStockRequest));
        return ResponseEntity.ok(PartRestMapper.toResponse(part));
    }
}
