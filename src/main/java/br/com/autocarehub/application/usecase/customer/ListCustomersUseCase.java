package br.com.autocarehub.application.usecase.customer;

import br.com.autocarehub.application.repository.CustomerRepository;
import br.com.autocarehub.domain.Customer;
import java.util.List;

public class ListCustomersUseCase {

    private final CustomerRepository customerRepository;

    public ListCustomersUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> execute() {
        return customerRepository.findAll();
    }
}
