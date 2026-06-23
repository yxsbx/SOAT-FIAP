package br.com.autocarehub.domain.model;

import java.util.Objects;
import java.util.UUID;

import br.com.autocarehub.domain.exception.DomainException;
import br.com.autocarehub.domain.service.DomainValidation;
import br.com.autocarehub.domain.valueobject.Money;

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
        this.name = DomainValidation.requireText(name, "Name is required", 100);
        this.description = DomainValidation.requireText(description, "Description is required", 500);
        this.basePrice = requirePositiveMoney(basePrice);
        this.estimatedTimeInMinutes = requireEstimatedTime(estimatedTimeInMinutes);
        this.active = active;
    }

    private static Money requirePositiveMoney(Money money) {
        Objects.requireNonNull(money, "money is required");
        if (money.isZeroOrNegative()) {
            throw new DomainException("Base price must be greater than zero");
        }
        return money;
    }

    private static int requireEstimatedTime(int value) {
        if (value <= 0) {
            throw new DomainException("Estimated time must be greater than zero");
        }
        return value;
    }

    public void update(String name, String description, Money basePrice, int estimatedTimeInMinutes) {
        this.name = DomainValidation.requireText(name, "Name is required", 100);
        this.description = DomainValidation.requireText(description, "Description is required", 500);
        this.basePrice = requirePositiveMoney(basePrice);
        this.estimatedTimeInMinutes = requireEstimatedTime(estimatedTimeInMinutes);
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
