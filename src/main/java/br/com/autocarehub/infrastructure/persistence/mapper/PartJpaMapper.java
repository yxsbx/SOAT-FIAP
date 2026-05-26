package br.com.autocarehub.infrastructure.persistence.mapper;

import br.com.autocarehub.domain.Money;
import br.com.autocarehub.domain.Part;
import br.com.autocarehub.infrastructure.persistence.entity.PartJpaEntity;

public final class PartJpaMapper {

    private PartJpaMapper() {
    }

    public static PartJpaEntity toEntity(Part part) {
        PartJpaEntity entity = new PartJpaEntity();
        entity.setId(part.id());
        entity.setName(part.name());
        entity.setDescription(part.description());
        entity.setSku(part.sku());
        entity.setCategory(part.category());
        entity.setSubcategory(part.subcategory());
        entity.setBrand(part.brand());
        entity.setCostPrice(part.costPrice().value());
        entity.setUnitPrice(part.unitPrice().value());
        entity.setStockQuantity(part.stockQuantity());
        entity.setReservedQuantity(part.reservedQuantity());
        entity.setMinimumStock(part.minimumStock());
        entity.setReservationDays(part.reservationDays());
        entity.setReservationExpiresAt(part.reservationExpiresAt());
        entity.setActive(part.active());
        return entity;
    }

    public static Part toDomain(PartJpaEntity entity) {
        return new Part(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getSku(),
                entity.getCategory(),
                entity.getSubcategory(),
                entity.getBrand(),
                new Money(entity.getCostPrice()),
                new Money(entity.getUnitPrice()),
                entity.getStockQuantity(),
                entity.getReservedQuantity(),
                entity.getMinimumStock(),
                entity.getReservationDays(),
                entity.getReservationExpiresAt(),
                entity.isActive());
    }
}
