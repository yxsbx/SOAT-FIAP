package br.com.autocarehub.infrastructure.persistence.mapper;

import br.com.autocarehub.domain.Address;
import br.com.autocarehub.domain.Customer;
import br.com.autocarehub.domain.Document;
import br.com.autocarehub.domain.DocumentType;
import br.com.autocarehub.infrastructure.persistence.entity.CustomerJpaEntity;

public final class CustomerJpaMapper {

    private CustomerJpaMapper() {
    }

    public static CustomerJpaEntity toEntity(Customer customer) {
        CustomerJpaEntity entity = new CustomerJpaEntity();
        entity.setId(customer.id());
        entity.setName(customer.name());
        entity.setDocumentType(customer.document().type().name());
        entity.setDocumentValue(customer.document().value());
        entity.setPhone(customer.phone());
        entity.setEmail(customer.email());
        entity.setActive(customer.active());
        entity.setCreatedAt(customer.createdAt());
        if (customer.address() != null) {
            entity.setAddressStreet(customer.address().street());
            entity.setAddressNumber(customer.address().number());
            entity.setAddressComplement(customer.address().complement());
            entity.setAddressNeighborhood(customer.address().neighborhood());
            entity.setAddressCity(customer.address().city());
            entity.setAddressState(customer.address().state());
            entity.setAddressZipCode(customer.address().zipCode());
        }
        return entity;
    }

    public static Customer toDomain(CustomerJpaEntity entity) {
        Address address = null;
        if (entity.getAddressStreet() != null) {
            address =
                    new Address(
                            entity.getAddressStreet(),
                            entity.getAddressNumber(),
                            entity.getAddressComplement(),
                            entity.getAddressNeighborhood(),
                            entity.getAddressCity(),
                            entity.getAddressState(),
                            entity.getAddressZipCode());
        }
        return new Customer(
                entity.getId(),
                entity.getName(),
                new Document(DocumentType.valueOf(entity.getDocumentType()), entity.getDocumentValue()),
                entity.getPhone(),
                entity.getEmail(),
                address,
                entity.isActive(),
                entity.getCreatedAt());
    }
}
