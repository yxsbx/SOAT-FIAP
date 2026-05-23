package br.com.autocarehub.domain;

import java.util.Objects;
import java.util.UUID;

public class Part {

    private final UUID id;
    private String name;
    private String sku;
    private String category;
    private String subcategory;
    private String brand;
    private Money unitPrice;
    private int stockQuantity;
    private int minimumStock;
    private boolean active;

    public Part(
            String name,
            String sku,
            String category,
            String subcategory,
            String brand,
            Money unitPrice,
            int stockQuantity,
            int minimumStock) {
        this(
                UUID.randomUUID(),
                name,
                sku,
                category,
                subcategory,
                brand,
                unitPrice,
                stockQuantity,
                minimumStock,
                true);
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
        this.id = Objects.requireNonNull(id, "id is required");
        this.name = requireText(name, "Name is required");
        this.sku = requireText(sku, "SKU is required");
        this.category = requireText(category, "Category is required");
        this.subcategory = subcategory == null ? null : subcategory.trim();
        this.brand = requireText(brand, "Brand is required");
        this.unitPrice = requirePositiveMoney(unitPrice, "Unit price must be greater than zero");
        this.stockQuantity = requireNonNegative(stockQuantity, "Stock cannot be negative");
        this.minimumStock = requireNonNegative(minimumStock, "Minimum stock cannot be negative");
        this.active = active;
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

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainException(message);
        }
        return value.trim();
    }

    public void update(
            String name,
            String sku,
            String category,
            String subcategory,
            String brand,
            Money unitPrice,
            int minimumStock) {
        this.name = requireText(name, "Name is required");
        this.sku = requireText(sku, "SKU is required");
        this.category = requireText(category, "Category is required");
        this.subcategory = subcategory == null ? null : subcategory.trim();
        this.brand = requireText(brand, "Brand is required");
        this.unitPrice = requirePositiveMoney(unitPrice, "Unit price must be greater than zero");
        this.minimumStock = requireNonNegative(minimumStock, "Minimum stock cannot be negative");
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
        if (quantity > stockQuantity) {
            throw new DomainException("Insufficient stock");
        }
        this.stockQuantity -= quantity;
    }

    public boolean hasAvailableStock(int quantity) {
        return quantity > 0 && stockQuantity >= quantity;
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

    public int stockQuantity() {
        return stockQuantity;
    }

    public int minimumStock() {
        return minimumStock;
    }

    public boolean active() {
        return active;
    }
}
