package br.com.autocarehub.infrastructure.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "service_orders")
public class ServiceOrderJpaEntity {

  @Id private UUID id;

  @Column(name = "customer_id", nullable = false)
  private UUID customerId;

  @Column(name = "vehicle_id", nullable = false)
  private UUID vehicleId;

  @Column(nullable = false, length = 30)
  private String status;

  @Column(name = "diagnostic_notes", nullable = false, length = 2000)
  private String diagnosticNotes;

  @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
  private BigDecimal totalAmount;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "budget_generated_at")
  private LocalDateTime budgetGeneratedAt;

  @Column(name = "approved_at")
  private LocalDateTime approvedAt;

  @Column(name = "started_at")
  private LocalDateTime startedAt;

  @Column(name = "finished_at")
  private LocalDateTime finishedAt;

  @Column(name = "delivered_at")
  private LocalDateTime deliveredAt;

  @OneToMany(mappedBy = "serviceOrder", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ServiceOrderServiceJpaEntity> services = new LinkedHashSet<>();

  @OneToMany(mappedBy = "serviceOrder", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ServiceOrderPartJpaEntity> parts = new LinkedHashSet<>();

  public ServiceOrderJpaEntity() {}

  public void replaceServices(List<ServiceOrderServiceJpaEntity> services) {
    this.services.clear();
    services.forEach(this::addService);
  }

  public void replaceParts(List<ServiceOrderPartJpaEntity> parts) {
    this.parts.clear();
    parts.forEach(this::addPart);
  }

  public void addService(ServiceOrderServiceJpaEntity service) {
    service.setServiceOrder(this);
    this.services.add(service);
  }

  public void addPart(ServiceOrderPartJpaEntity part) {
    part.setServiceOrder(this);
    this.parts.add(part);
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getCustomerId() {
    return customerId;
  }

  public void setCustomerId(UUID customerId) {
    this.customerId = customerId;
  }

  public UUID getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(UUID vehicleId) {
    this.vehicleId = vehicleId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getDiagnosticNotes() {
    return diagnosticNotes;
  }

  public void setDiagnosticNotes(String diagnosticNotes) {
    this.diagnosticNotes = diagnosticNotes;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getBudgetGeneratedAt() {
    return budgetGeneratedAt;
  }

  public void setBudgetGeneratedAt(LocalDateTime budgetGeneratedAt) {
    this.budgetGeneratedAt = budgetGeneratedAt;
  }

  public LocalDateTime getApprovedAt() {
    return approvedAt;
  }

  public void setApprovedAt(LocalDateTime approvedAt) {
    this.approvedAt = approvedAt;
  }

  public LocalDateTime getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(LocalDateTime startedAt) {
    this.startedAt = startedAt;
  }

  public LocalDateTime getFinishedAt() {
    return finishedAt;
  }

  public void setFinishedAt(LocalDateTime finishedAt) {
    this.finishedAt = finishedAt;
  }

  public LocalDateTime getDeliveredAt() {
    return deliveredAt;
  }

  public void setDeliveredAt(LocalDateTime deliveredAt) {
    this.deliveredAt = deliveredAt;
  }

  public List<ServiceOrderServiceJpaEntity> getServices() {
    return new ArrayList<>(services);
  }

  public List<ServiceOrderPartJpaEntity> getParts() {
    return new ArrayList<>(parts);
  }
}
