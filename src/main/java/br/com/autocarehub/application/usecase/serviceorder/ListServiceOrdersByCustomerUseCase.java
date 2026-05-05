package br.com.autocarehub.application.usecase.serviceorder;

import br.com.autocarehub.application.ResourceNotFoundException;
import br.com.autocarehub.application.repository.CustomerRepository;
import br.com.autocarehub.application.repository.ServiceOrderRepository;
import br.com.autocarehub.domain.ServiceOrder;
import java.util.List;
import java.util.UUID;

public class ListServiceOrdersByCustomerUseCase {

    private final ServiceOrderRepository serviceOrderRepository;
    private final CustomerRepository customerRepository;

    public ListServiceOrdersByCustomerUseCase(ServiceOrderRepository serviceOrderRepository, CustomerRepository customerRepository) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.customerRepository = customerRepository;
    }

    public List<ServiceOrder> execute(UUID customerId) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return serviceOrderRepository.findByCustomerId(customerId);
    }
}
