package br.com.autocarehub.application.usecase.customer;

import br.com.autocarehub.application.ResourceNotFoundException;
import br.com.autocarehub.application.repository.CustomerRepository;
import br.com.autocarehub.domain.Customer;
import java.util.UUID;

public class FindCustomerUseCase {

    private final CustomerRepository customerRepository;

    public FindCustomerUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer execute(UUID customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }
}
