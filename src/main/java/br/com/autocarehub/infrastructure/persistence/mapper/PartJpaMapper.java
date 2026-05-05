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
        entity.setSku(part.sku());
        entity.setCategory(part.category());
        entity.setSubcategory(part.subcategory());
        entity.setBrand(part.brand());
        entity.setUnitPrice(part.unitPrice().value());
        entity.setStockQuantity(part.stockQuantity());
        entity.setMinimumStock(part.minimumStock());
        entity.setActive(part.active());
        return entity;
    }

    public static Part toDomain(PartJpaEntity entity) {
        return new Part(
                entity.getId(),
                entity.getName(),
                entity.getSku(),
                entity.getCategory(),
                entity.getSubcategory(),
                entity.getBrand(),
                new Money(entity.getUnitPrice()),
                entity.getStockQuantity(),
                entity.getMinimumStock(),
                entity.isActive()
        );
    }
}
