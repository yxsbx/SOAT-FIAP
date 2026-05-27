package br.com.autocarehub.interfaces.rest.impl.mapper;

import br.com.autocarehub.application.usecase.customer.CreateCustomerUseCase;
import br.com.autocarehub.application.usecase.customer.ListCustomersUseCase;
import br.com.autocarehub.application.usecase.customer.UpdateCustomerUseCase;
import br.com.autocarehub.domain.Customer;
import br.com.autocarehub.interfaces.rest.generated.model.CreateCustomerRequest;
import br.com.autocarehub.interfaces.rest.generated.model.CustomerListResponse;
import br.com.autocarehub.interfaces.rest.generated.model.CustomerResponse;
import br.com.autocarehub.interfaces.rest.generated.model.UpdateCustomerRequest;

import java.util.List;
import java.util.UUID;

public final class CustomerRestMapper {

    private CustomerRestMapper() {
    }

    public static CreateCustomerUseCase.Command toCommand(CreateCustomerRequest request) {
        return new CreateCustomerUseCase.Command(
                request.getName(),
                request.getDocument(),
                request.getPhone(),
                request.getEmail(),
                toDomainAddress(request.getAddress()));
    }

    public static UpdateCustomerUseCase.Command toCommand(
            UUID customerId, UpdateCustomerRequest request) {
        return new UpdateCustomerUseCase.Command(
                customerId,
                request.getName(),
                request.getPhone(),
                request.getEmail(),
                toDomainAddress(request.getAddress()),
                Boolean.TRUE.equals(request.getActive()));
    }

    public static ListCustomersUseCase.Query toQuery(Boolean active) {
        return new ListCustomersUseCase.Query(active);
    }

    public static CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.id(),
                customer.name(),
                customer.document().value(),
                customer.phone(),
                customer.email(),
                toApiAddress(customer.address()),
                customer.active(),
                RestMapperSupport.toOffsetDateTime(customer.createdAt()));
    }

    public static CustomerListResponse toListResponse(
            List<Customer> customers, Integer page, Integer size) {
        List<CustomerResponse> items =
                RestMapperSupport.page(customers, page, size).stream()
                        .map(CustomerRestMapper::toResponse)
                        .toList();

        return new CustomerListResponse(
                items,
                page == null ? 0 : page,
                size == null ? customers.size() : size,
                (long) customers.size(),
                RestMapperSupport.totalPages(customers.size(), size));
    }

    public static br.com.autocarehub.domain.Address toDomainAddress(
            br.com.autocarehub.interfaces.rest.generated.model.Address address) {
        if (address == null) {
            return null;
        }
        assert address.getComplement() != null;
        return new br.com.autocarehub.domain.Address(
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getZipCode());
    }

    public static br.com.autocarehub.interfaces.rest.generated.model.Address toApiAddress(
            br.com.autocarehub.domain.Address address) {
        if (address == null) {
            return null;
        }
        return new br.com.autocarehub.interfaces.rest.generated.model.Address(
                address.street(),
                address.number(),
                address.neighborhood(),
                address.city(),
                address.state(),
                address.zipCode())
                .complement(address.complement());
    }
}
