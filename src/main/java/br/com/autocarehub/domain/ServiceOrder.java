package br.com.autocarehub.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ServiceOrder {

    private final UUID id;
    private final UUID customerId;
    private final UUID vehicleId;
    private ServiceOrderStatus status;
    private String diagnosticNotes;
    private final List<ServiceOrderService> services;
    private final List<ServiceOrderPart> parts;
    private Money totalAmount;
    private final LocalDateTime createdAt;
    private LocalDateTime budgetGeneratedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private LocalDateTime deliveredAt;

    public ServiceOrder(UUID customerId, UUID vehicleId, String diagnosticNotes) {
        this(UUID.randomUUID(), customerId, vehicleId, ServiceOrderStatus.RECEIVED, diagnosticNotes, List.of(), List.of(), Money.zero(), LocalDateTime.now(), null, null, null, null, null);
    }

    public ServiceOrder(
            UUID id,
            UUID customerId,
            UUID vehicleId,
            ServiceOrderStatus status,
            String diagnosticNotes,
            List<ServiceOrderService> services,
            List<ServiceOrderPart> parts,
            Money totalAmount,
            LocalDateTime createdAt,
            LocalDateTime budgetGeneratedAt,
            LocalDateTime approvedAt,
            LocalDateTime startedAt,
            LocalDateTime finishedAt,
            LocalDateTime deliveredAt
    ) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.customerId = Objects.requireNonNull(customerId, "customerId is required");
        this.vehicleId = Objects.requireNonNull(vehicleId, "vehicleId is required");
        this.status = Objects.requireNonNull(status, "status is required");
        this.diagnosticNotes = requireText(diagnosticNotes, "Diagnostic notes are required");
        this.services = new ArrayList<>(Objects.requireNonNull(services, "services are required"));
        this.parts = new ArrayList<>(Objects.requireNonNull(parts, "parts are required"));
        this.totalAmount = Objects.requireNonNull(totalAmount, "totalAmount is required");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.budgetGeneratedAt = budgetGeneratedAt;
        this.approvedAt = approvedAt;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.deliveredAt = deliveredAt;
    }

    public void startDiagnosis() {
        requireStatus(ServiceOrderStatus.RECEIVED, "Diagnosis can only start from received orders");
        this.status = ServiceOrderStatus.IN_DIAGNOSIS;
    }

    public void updateDiagnosticNotes(String diagnosticNotes) {
        this.diagnosticNotes = requireText(diagnosticNotes, "Diagnostic notes are required");
    }

    public void addService(WorkshopService service, int quantity) {
        Objects.requireNonNull(service, "service is required");
        ensureCanChangeBudgetItems();
        this.services.add(new ServiceOrderService(service.id(), service.name(), quantity, service.basePrice()));
    }

    public void addPart(Part part, int quantity) {
        Objects.requireNonNull(part, "part is required");
        ensureCanChangeBudgetItems();
        if (!part.hasAvailableStock(quantity)) {
            throw new DomainException("Part stock is not available");
        }
        this.parts.add(new ServiceOrderPart(part.id(), part.name(), part.sku(), quantity, part.unitPrice()));
    }

    public Money generateBudget() {
        if (services.isEmpty() && parts.isEmpty()) {
            throw new DomainException("Budget requires at least one service or part");
        }
        this.totalAmount = calculateTotalAmount();
        this.budgetGeneratedAt = LocalDateTime.now();
        this.status = ServiceOrderStatus.WAITING_APPROVAL;
        return totalAmount;
    }

    public void approveBudget() {
        requireStatus(ServiceOrderStatus.WAITING_APPROVAL, "Budget can only be approved while waiting approval");
        if (budgetGeneratedAt == null) {
            throw new DomainException("Budget must be generated before approval");
        }
        this.approvedAt = LocalDateTime.now();
    }

    public void startExecution() {
        requireStatus(ServiceOrderStatus.WAITING_APPROVAL, "Execution can only start after budget generation");
        if (approvedAt == null) {
            throw new DomainException("Execution cannot start without budget approval");
        }
        this.status = ServiceOrderStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }

    public void finish() {
        requireStatus(ServiceOrderStatus.IN_PROGRESS, "Service order can only be finished while in progress");
        this.status = ServiceOrderStatus.FINISHED;
        this.finishedAt = LocalDateTime.now();
    }

    public void deliver() {
        requireStatus(ServiceOrderStatus.FINISHED, "Service order can only be delivered after finished");
        this.status = ServiceOrderStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    public UUID id() {
        return id;
    }

    public UUID customerId() {
        return customerId;
    }

    public UUID vehicleId() {
        return vehicleId;
    }

    public ServiceOrderStatus status() {
        return status;
    }

    public String diagnosticNotes() {
        return diagnosticNotes;
    }

    public List<ServiceOrderService> services() {
        return Collections.unmodifiableList(services);
    }

    public List<ServiceOrderPart> parts() {
        return Collections.unmodifiableList(parts);
    }

    public Money totalAmount() {
        return totalAmount;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public LocalDateTime budgetGeneratedAt() {
        return budgetGeneratedAt;
    }

    public LocalDateTime approvedAt() {
        return approvedAt;
    }

    public LocalDateTime startedAt() {
        return startedAt;
    }

    public LocalDateTime finishedAt() {
        return finishedAt;
    }

    public LocalDateTime deliveredAt() {
        return deliveredAt;
    }

    private Money calculateTotalAmount() {
        Money servicesTotal = services.stream()
                .map(ServiceOrderService::totalPrice)
                .reduce(Money.zero(), Money::add);
        Money partsTotal = parts.stream()
                .map(ServiceOrderPart::totalPrice)
                .reduce(Money.zero(), Money::add);
        return servicesTotal.add(partsTotal);
    }

    private void ensureCanChangeBudgetItems() {
        if (status == ServiceOrderStatus.WAITING_APPROVAL
                || status == ServiceOrderStatus.IN_PROGRESS
                || status == ServiceOrderStatus.FINISHED
                || status == ServiceOrderStatus.DELIVERED) {
            throw new DomainException("Service order items cannot be changed in current status");
        }
    }

    private void requireStatus(ServiceOrderStatus expected, String message) {
        if (status != expected) {
            throw new DomainException(message);
        }
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainException(message);
        }
        return value.trim();
    }

    public record ServiceOrderService(UUID serviceId, String name, int quantity, Money unitPrice, Money totalPrice) {

        public ServiceOrderService(UUID serviceId, String name, int quantity, Money unitPrice) {
            this(serviceId, name, quantity, unitPrice, unitPrice.multiply(quantity));
        }

        public ServiceOrderService {
            Objects.requireNonNull(serviceId, "serviceId is required");
            name = requireText(name, "Service name is required");
            if (quantity <= 0) {
                throw new DomainException("Quantity must be greater than zero");
            }
            Objects.requireNonNull(unitPrice, "unitPrice is required");
            Objects.requireNonNull(totalPrice, "totalPrice is required");
        }
    }

    public record ServiceOrderPart(UUID partId, String name, String sku, int quantity, Money unitPrice, Money totalPrice) {

        public ServiceOrderPart(UUID partId, String name, String sku, int quantity, Money unitPrice) {
            this(partId, name, sku, quantity, unitPrice, unitPrice.multiply(quantity));
        }

        public ServiceOrderPart {
            Objects.requireNonNull(partId, "partId is required");
            name = requireText(name, "Part name is required");
            sku = requireText(sku, "SKU is required");
            if (quantity <= 0) {
                throw new DomainException("Quantity must be greater than zero");
            }
            Objects.requireNonNull(unitPrice, "unitPrice is required");
            Objects.requireNonNull(totalPrice, "totalPrice is required");
        }
    }
}
