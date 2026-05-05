package br.com.autocarehub.domain;

import java.util.Objects;
import java.util.UUID;

public class Vehicle {

    private final UUID id;
    private final UUID customerId;
    private Plate plate;
    private String brand;
    private String model;
    private int year;
    private int mileage;
    private boolean active;

    public Vehicle(UUID customerId, Plate plate, String brand, String model, int year, int mileage) {
        this(UUID.randomUUID(), customerId, plate, brand, model, year, mileage, true);
    }

    public Vehicle(UUID id, UUID customerId, Plate plate, String brand, String model, int year, int mileage, boolean active) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.customerId = Objects.requireNonNull(customerId, "customerId is required");
        this.plate = Objects.requireNonNull(plate, "plate is required");
        this.brand = requireText(brand, "Brand is required");
        this.model = requireText(model, "Model is required");
        this.year = requireYear(year);
        this.mileage = requireMileage(mileage);
        this.active = active;
    }

    public void update(Plate plate, String brand, String model, int year, int mileage) {
        this.plate = Objects.requireNonNull(plate, "plate is required");
        this.brand = requireText(brand, "Brand is required");
        this.model = requireText(model, "Model is required");
        this.year = requireYear(year);
        this.mileage = requireMileage(mileage);
    }

    public void updateMileage(int mileage) {
        if (mileage < this.mileage) {
            throw new DomainException("Mileage cannot decrease");
        }
        this.mileage = requireMileage(mileage);
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

    public UUID customerId() {
        return customerId;
    }

    public Plate plate() {
        return plate;
    }

    public String brand() {
        return brand;
    }

    public String model() {
        return model;
    }

    public int year() {
        return year;
    }

    public int mileage() {
        return mileage;
    }

    public boolean active() {
        return active;
    }

    private static int requireYear(int value) {
        if (value < 1900) {
            throw new DomainException("Invalid year");
        }
        return value;
    }

    private static int requireMileage(int value) {
        if (value < 0) {
            throw new DomainException("Mileage cannot be negative");
        }
        return value;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainException(message);
        }
        return value.trim();
    }
}
