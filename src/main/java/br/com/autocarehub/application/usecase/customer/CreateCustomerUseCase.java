package br.com.autocarehub.application.usecase.customer;

import br.com.autocarehub.application.repository.CustomerRepository;
import br.com.autocarehub.domain.Address;
import br.com.autocarehub.domain.Customer;
import br.com.autocarehub.domain.Document;

public class CreateCustomerUseCase {

    private final CustomerRepository customerRepository;

    public CreateCustomerUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer execute(Command command) {
        Customer customer =
                new Customer(
                        command.name(),
                        Document.from(command.document()),
                        command.phone(),
                        command.email(),
                        command.address());
        return customerRepository.save(customer);
    }

    public record Command(
            String name, String document, String phone, String email, Address address) {
    }
}
