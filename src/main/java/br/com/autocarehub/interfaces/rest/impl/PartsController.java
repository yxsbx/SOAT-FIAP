package br.com.autocarehub.interfaces.rest.impl;

import br.com.autocarehub.application.usecase.part.*;
import br.com.autocarehub.domain.Money;
import br.com.autocarehub.domain.Part;
import br.com.autocarehub.interfaces.rest.generated.api.PartsApi;
import br.com.autocarehub.interfaces.rest.generated.model.*;
import br.com.autocarehub.interfaces.rest.impl.mapper.PartRestMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class PartsController implements PartsApi {

    private final CreatePartUseCase createPartUseCase;
    private final UpdatePartUseCase updatePartUseCase;
    private final FindPartUseCase findPartUseCase;
    private final ListPartsUseCase listPartsUseCase;
    private final DeletePartUseCase deletePartUseCase;
    private final UpdatePartStockUseCase updatePartStockUseCase;
    private final RegisterPartStockMovementUseCase registerPartStockMovementUseCase;
    private final ConfigurePartReservationUseCase configurePartReservationUseCase;

    public PartsController(
            CreatePartUseCase createPartUseCase,
            UpdatePartUseCase updatePartUseCase,
            FindPartUseCase findPartUseCase,
            ListPartsUseCase listPartsUseCase,
            DeletePartUseCase deletePartUseCase,
            UpdatePartStockUseCase updatePartStockUseCase,
            RegisterPartStockMovementUseCase registerPartStockMovementUseCase,
            ConfigurePartReservationUseCase configurePartReservationUseCase) {
        this.createPartUseCase = createPartUseCase;
        this.updatePartUseCase = updatePartUseCase;
        this.findPartUseCase = findPartUseCase;
        this.listPartsUseCase = listPartsUseCase;
        this.deletePartUseCase = deletePartUseCase;
        this.updatePartStockUseCase = updatePartStockUseCase;
        this.registerPartStockMovementUseCase = registerPartStockMovementUseCase;
        this.configurePartReservationUseCase = configurePartReservationUseCase;
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
    public ResponseEntity<PartListResponse> listParts(
            Integer page, Integer size, Boolean active, Boolean lowStock) {
        return ResponseEntity.ok(
                PartRestMapper.toListResponse(
                        listPartsUseCase.execute(PartRestMapper.toQuery(active, lowStock)), page, size));
    }

    @Override
    public ResponseEntity<PartResponse> updatePart(UUID partId, UpdatePartRequest updatePartRequest) {
        Part part = updatePartUseCase.execute(PartRestMapper.toCommand(partId, updatePartRequest));
        return ResponseEntity.ok(PartRestMapper.toResponse(part));
    }

    @Override
    public ResponseEntity<PartResponse> updatePartStock(
            UUID partId, UpdatePartStockRequest updatePartStockRequest) {
        Part part =
                updatePartStockUseCase.execute(PartRestMapper.toCommand(partId, updatePartStockRequest));
        return ResponseEntity.ok(PartRestMapper.toResponse(part));
    }

    @PatchMapping("/api/v1/parts/{partId}/stock-movement")
    public ResponseEntity<PartResponse> registerStockMovement(
            @PathVariable UUID partId, @Valid @RequestBody StockMovementRequest request) {
        Part part =
                registerPartStockMovementUseCase.execute(
                        new RegisterPartStockMovementUseCase.Command(
                                partId,
                                RegisterPartStockMovementUseCase.MovementType.valueOf(request.type()),
                                request.quantity(),
                                request.unitCost() == null ? null : new Money(request.unitCost()),
                                request.unitPrice() == null ? null : new Money(request.unitPrice()),
                                request.reason()));
        return ResponseEntity.ok(PartRestMapper.toResponse(part));
    }

    @PatchMapping("/api/v1/parts/{partId}/reservation")
    public ResponseEntity<PartResponse> configureReservation(
            @PathVariable UUID partId, @Valid @RequestBody ReservationRequest request) {
        Part part =
                configurePartReservationUseCase.execute(
                        new ConfigurePartReservationUseCase.Command(partId, request.reservationDays()));
        return ResponseEntity.ok(PartRestMapper.toResponse(part));
    }

    public record StockMovementRequest(
            @NotBlank String type,
            @Min(1) int quantity,
            BigDecimal unitCost,
            BigDecimal unitPrice,
            String reason) {}

    public record ReservationRequest(@Min(1) int reservationDays) {}
}
