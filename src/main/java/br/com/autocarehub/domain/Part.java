package br.com.autocarehub.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Part {

  private final UUID id;
  private String name;
  private String description;
  private String sku;
  private String category;
  private String subcategory;
  private String brand;
  private Money costPrice;
  private Money unitPrice;
  private int stockQuantity;
  private int reservedQuantity;
  private int minimumStock;
  private int reservationDays;
  private LocalDateTime reservationExpiresAt;
  private boolean active;

  public Part(
      String name,
      String description,
      String sku,
      String category,
      String subcategory,
      String brand,
      Money unitPrice,
      int stockQuantity,
      int minimumStock) {
    this(
        name,
        description,
        sku,
        category,
        subcategory,
        brand,
        Money.zero(),
        unitPrice,
        stockQuantity,
        minimumStock);
  }

  public Part(
      String name,
      String sku,
      String category,
      String subcategory,
      String brand,
      Money unitPrice,
      int stockQuantity,
      int minimumStock) {
    this(name, name, sku, category, subcategory, brand, unitPrice, stockQuantity, minimumStock);
  }

  public Part(
      String name,
      String description,
      String sku,
      String category,
      String subcategory,
      String brand,
      Money costPrice,
      Money unitPrice,
      int stockQuantity,
      int minimumStock) {
    this(
        UUID.randomUUID(),
        name,
        description,
        sku,
        category,
        subcategory,
        brand,
        costPrice,
        unitPrice,
        stockQuantity,
        0,
        minimumStock,
        3,
        null,
        true);
  }

  public Part(
      String name,
      String sku,
      String category,
      String subcategory,
      String brand,
      Money costPrice,
      Money unitPrice,
      int stockQuantity,
      int minimumStock) {
    this(
        name,
        name,
        sku,
        category,
        subcategory,
        brand,
        costPrice,
        unitPrice,
        stockQuantity,
        minimumStock);
  }

  public Part(
      UUID id,
      String name,
      String description,
      String sku,
      String category,
      String subcategory,
      String brand,
      Money unitPrice,
      int stockQuantity,
      int minimumStock,
      boolean active) {
    this(
        id,
        name,
        description,
        sku,
        category,
        subcategory,
        brand,
        Money.zero(),
        unitPrice,
        stockQuantity,
        0,
        minimumStock,
        3,
        null,
        active);
  }

  public Part(
      UUID id,
      String name,
      String sku,
      String category,
      String subcategory,
      String brand,
      Money unitPrice,
      int stockQuantity,
      int minimumStock,
      boolean active) {
    this(
        id,
        name,
        name,
        sku,
        category,
        subcategory,
        brand,
        unitPrice,
        stockQuantity,
        minimumStock,
        active);
  }

  public Part(
      UUID id,
      String name,
      String description,
      String sku,
      String category,
      String subcategory,
      String brand,
      Money costPrice,
      Money unitPrice,
      int stockQuantity,
      int reservedQuantity,
      int minimumStock,
      int reservationDays,
      LocalDateTime reservationExpiresAt,
      boolean active) {
    this.id = Objects.requireNonNull(id, "id is required");
    this.name = DomainValidation.requireText(name, "Name is required", 120);
    this.description = DomainValidation.requireText(description, "Description is required", 500);
    this.sku = DomainValidation.requireText(sku, "SKU is required", 60);
    this.category = DomainValidation.requireText(category, "Category is required", 80);
    this.subcategory = DomainValidation.optionalText(subcategory, 80);
    this.brand = DomainValidation.requireText(brand, "Brand is required", 80);
    this.costPrice = requireNonNegativeMoney(costPrice, "Cost price cannot be negative");
    this.unitPrice = requirePositiveMoney(unitPrice, "Unit price must be greater than zero");
    this.stockQuantity = requireNonNegative(stockQuantity, "Stock cannot be negative");
    this.reservedQuantity =
        requireNonNegative(reservedQuantity, "Reserved stock cannot be negative");
    if (reservedQuantity > stockQuantity) {
      throw new DomainException("Reserved stock cannot be greater than stock");
    }
    this.minimumStock = requireNonNegative(minimumStock, "Minimum stock cannot be negative");
    this.reservationDays = reservationDays <= 0 ? 3 : reservationDays;
    this.reservationExpiresAt = reservationExpiresAt;
    this.active = active;
  }

  private static Money requireNonNegativeMoney(Money money, String message) {
    Money normalized = money == null ? Money.zero() : money;
    if (normalized.value().signum() < 0) {
      throw new DomainException(message);
    }
    return normalized;
  }

  private static Money requirePositiveMoney(Money money, String message) {
    Objects.requireNonNull(money, "money is required");
    if (!money.isGreaterThanZero()) {
      throw new DomainException(message);
    }
    return money;
  }

  private static int requireNonNegative(int value, String message) {
    if (value < 0) {
      throw new DomainException(message);
    }
    return value;
  }

  public void update(
      String name,
      String description,
      String sku,
      String category,
      String subcategory,
      String brand,
      Money costPrice,
      Money unitPrice,
      int minimumStock) {
    this.name = DomainValidation.requireText(name, "Name is required", 120);
    this.description = DomainValidation.requireText(description, "Description is required", 500);
    this.sku = DomainValidation.requireText(sku, "SKU is required", 60);
    this.category = DomainValidation.requireText(category, "Category is required", 80);
    this.subcategory = DomainValidation.optionalText(subcategory, 80);
    this.brand = DomainValidation.requireText(brand, "Brand is required", 80);
    this.costPrice = requireNonNegativeMoney(costPrice, "Cost price cannot be negative");
    this.unitPrice = requirePositiveMoney(unitPrice, "Unit price must be greater than zero");
    this.minimumStock = requireNonNegative(minimumStock, "Minimum stock cannot be negative");
  }

  public void update(
      String name,
      String description,
      String sku,
      String category,
      String subcategory,
      String brand,
      Money unitPrice,
      int minimumStock) {
    update(
        name,
        description,
        sku,
        category,
        subcategory,
        brand,
        this.costPrice,
        unitPrice,
        minimumStock);
  }

  public void update(
      String name,
      String sku,
      String category,
      String subcategory,
      String brand,
      Money unitPrice,
      int minimumStock) {
    update(name, name, sku, category, subcategory, brand, this.costPrice, unitPrice, minimumStock);
  }

  public void update(
      String name,
      String sku,
      String category,
      String subcategory,
      String brand,
      Money costPrice,
      Money unitPrice,
      int minimumStock) {
    update(name, name, sku, category, subcategory, brand, costPrice, unitPrice, minimumStock);
  }

  public void increaseStock(int quantity) {
    if (quantity <= 0) {
      throw new DomainException("Quantity must be greater than zero");
    }
    this.stockQuantity += quantity;
  }

  public void reduceStock(int quantity) {
    if (quantity <= 0) {
      throw new DomainException("Quantity must be greater than zero");
    }
    if (quantity > availableQuantity()) {
      throw new DomainException("Insufficient stock");
    }
    this.stockQuantity -= quantity;
  }

  public boolean hasAvailableStock(int quantity) {
    releaseExpiredReservation();
    return quantity > 0 && availableQuantity() >= quantity;
  }

  public void reserveStock(int quantity) {
    if (quantity <= 0) {
      throw new DomainException("Quantity must be greater than zero");
    }
    releaseExpiredReservation();
    if (quantity > availableQuantity()) {
      throw new DomainException("Insufficient stock");
    }
    this.reservedQuantity += quantity;
    this.reservationExpiresAt = LocalDateTime.now().plusDays(reservationDays);
  }

  public void commitReservedStock(int quantity) {
    if (quantity <= 0) {
      throw new DomainException("Quantity must be greater than zero");
    }
    releaseExpiredReservation();
    int quantityToCommit = Math.min(quantity, reservedQuantity);
    int remainingQuantity = quantity - quantityToCommit;
    if (remainingQuantity > availableQuantity()) {
      throw new DomainException("Insufficient stock");
    }
    this.reservedQuantity -= quantityToCommit;
    this.stockQuantity -= quantity;
    if (reservedQuantity == 0) {
      this.reservationExpiresAt = null;
    }
  }

  public void releaseReservedStock(int quantity) {
    if (quantity <= 0) {
      throw new DomainException("Quantity must be greater than zero");
    }
    this.reservedQuantity = Math.max(0, reservedQuantity - quantity);
    if (reservedQuantity == 0) {
      this.reservationExpiresAt = null;
    }
  }

  public void releaseExpiredReservation() {
    if (reservationExpiresAt != null && reservationExpiresAt.isBefore(LocalDateTime.now())) {
      reservedQuantity = 0;
      reservationExpiresAt = null;
    }
  }

  public void configureReservationDays(int reservationDays) {
    if (reservationDays <= 0) {
      throw new DomainException("Reservation days must be greater than zero");
    }
    this.reservationDays = reservationDays;
  }

  public int availableQuantity() {
    releaseExpiredReservation();
    return Math.max(0, stockQuantity - reservedQuantity);
  }

  public String stockStatus() {
    if (!active) {
      return "INACTIVE";
    }
    if (availableQuantity() <= 0) {
      return "OUT_OF_STOCK";
    }
    if (availableQuantity() <= minimumStock) {
      return "LOW_STOCK";
    }
    if (reservedQuantity > 0) {
      return "RESERVED";
    }
    return "AVAILABLE";
  }

  public void activate() {
    this.active = true;
  }

  public void deactivate() {
    this.active = false;
  }

  public UUID id() {
    return id;
  }

  public String name() {
    return name;
  }

  public String description() {
    return description;
  }

  public String sku() {
    return sku;
  }

  public String category() {
    return category;
  }

  public String subcategory() {
    return subcategory;
  }

  public String brand() {
    return brand;
  }

  public Money unitPrice() {
    return unitPrice;
  }

  public Money costPrice() {
    return costPrice;
  }

  public int stockQuantity() {
    return stockQuantity;
  }

  public int reservedQuantity() {
    return reservedQuantity;
  }

  public int minimumStock() {
    return minimumStock;
  }

  public int reservationDays() {
    return reservationDays;
  }

  public LocalDateTime reservationExpiresAt() {
    return reservationExpiresAt;
  }

  public boolean active() {
    return active;
  }
}
