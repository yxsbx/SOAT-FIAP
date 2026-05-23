package br.com.autocarehub.domain;

import java.util.Objects;
import java.util.UUID;

public class WorkshopService {

    private final UUID id;
    private String name;
    private String description;
    private Money basePrice;
    private int estimatedTimeInMinutes;
    private boolean active;

    public WorkshopService(
            String name, String description, Money basePrice, int estimatedTimeInMinutes) {
        this(UUID.randomUUID(), name, description, basePrice, estimatedTimeInMinutes, true);
    }

    public WorkshopService(
            UUID id,
            String name,
            String description,
            Money basePrice,
            int estimatedTimeInMinutes,
            boolean active) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.name = requireText(name, "Name is required");
        this.description = requireText(description, "Description is required");
        this.basePrice = requirePositiveMoney(basePrice, "Base price must be greater than zero");
        this.estimatedTimeInMinutes =
                requirePositive(estimatedTimeInMinutes, "Estimated time must be greater than zero");
        this.active = active;
    }

    private static Money requirePositiveMoney(Money money, String message) {
        Objects.requireNonNull(money, "money is required");
        if (!money.isGreaterThanZero()) {
            throw new DomainException(message);
        }
        return money;
    }

    private static int requirePositive(int value, String message) {
        if (value <= 0) {
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

    public void update(String name, String description, Money basePrice, int estimatedTimeInMinutes) {
        this.name = requireText(name, "Name is required");
        this.description = requireText(description, "Description is required");
        this.basePrice = requirePositiveMoney(basePrice, "Base price must be greater than zero");
        this.estimatedTimeInMinutes =
                requirePositive(estimatedTimeInMinutes, "Estimated time must be greater than zero");
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

    public Money basePrice() {
        return basePrice;
    }

    public int estimatedTimeInMinutes() {
        return estimatedTimeInMinutes;
    }

    public boolean active() {
        return active;
    }
}
