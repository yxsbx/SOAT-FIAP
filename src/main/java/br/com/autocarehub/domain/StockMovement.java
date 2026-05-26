package br.com.autocarehub.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record StockMovement(
        UUID partId,
        StockMovementType type,
        int quantity,
        Money unitCost,
        Money unitPrice,
        String reason,
        LocalDateTime occurredAt) {

    public StockMovement {
        Objects.requireNonNull(partId, "partId is required");
        Objects.requireNonNull(type, "type is required");
        if (quantity <= 0) {
            throw new DomainException("Quantity must be greater than zero");
        }
        unitCost = unitCost == null ? Money.zero() : unitCost;
        unitPrice = unitPrice == null ? Money.zero() : unitPrice;
        reason = reason == null ? null : reason.trim();
        occurredAt = occurredAt == null ? LocalDateTime.now() : occurredAt;
    }
}
