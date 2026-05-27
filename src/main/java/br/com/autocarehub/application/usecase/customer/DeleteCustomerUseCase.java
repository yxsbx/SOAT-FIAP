package br.com.autocarehub.application.usecase.customer;

import br.com.autocarehub.application.ResourceNotFoundException;
import br.com.autocarehub.application.repository.CustomerRepository;
import br.com.autocarehub.domain.Customer;

import java.util.UUID;

public class DeleteCustomerUseCase {

    private final CustomerRepository customerRepository;

    public DeleteCustomerUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void execute(UUID customerId) {
        Customer customer =
                customerRepository
                        .findById(customerId)
                        .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        customer.deactivate();
        customerRepository.save(customer);
    }
}
