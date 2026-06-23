package br.com.autocarehub.application.usecase.serviceorder;

import java.util.List;
import java.util.UUID;

import br.com.autocarehub.application.exception.ResourceNotFoundException;
import br.com.autocarehub.application.port.out.CustomerRepository;
import br.com.autocarehub.application.port.out.ServiceOrderRepository;
import br.com.autocarehub.domain.model.ServiceOrder;

public class ListServiceOrdersByCustomerUseCase {

    private final ServiceOrderRepository serviceOrderRepository;
    private final CustomerRepository customerRepository;

    public ListServiceOrdersByCustomerUseCase(
            ServiceOrderRepository serviceOrderRepository, CustomerRepository customerRepository) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.customerRepository = customerRepository;
    }

    public List<ServiceOrder> execute(UUID customerId) {
        customerRepository
                .findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return serviceOrderRepository.findByCustomerId(customerId);
    }
}
