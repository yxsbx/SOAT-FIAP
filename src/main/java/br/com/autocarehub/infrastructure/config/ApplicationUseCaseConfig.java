package br.com.autocarehub.infrastructure.config;

import br.com.autocarehub.application.repository.*;
import br.com.autocarehub.application.usecase.auth.LoginUseCase;
import br.com.autocarehub.application.usecase.customer.*;
import br.com.autocarehub.application.usecase.demo.ListDemoLeadsUseCase;
import br.com.autocarehub.application.usecase.demo.RegisterDemoLeadUseCase;
import br.com.autocarehub.application.usecase.part.*;
import br.com.autocarehub.application.usecase.serviceorder.*;
import br.com.autocarehub.application.usecase.vehicle.*;
import br.com.autocarehub.application.usecase.workshopservice.*;
import br.com.autocarehub.infrastructure.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

@Configuration
public class ApplicationUseCaseConfig {

    @Bean
    LoginUseCase loginUseCase(AuthenticationManager authenticationManager, JwtService jwtService) {
        return new LoginUseCase(authenticationManager, jwtService);
    }

    @Bean
    RegisterDemoLeadUseCase registerDemoLeadUseCase(DemoLeadRepository repository) {
        return new RegisterDemoLeadUseCase(repository);
    }

    @Bean
    ListDemoLeadsUseCase listDemoLeadsUseCase(DemoLeadRepository repository) {
        return new ListDemoLeadsUseCase(repository);
    }

    @Bean
    CreateCustomerUseCase createCustomerUseCase(CustomerRepository repository) {
        return new CreateCustomerUseCase(repository);
    }

    @Bean
    UpdateCustomerUseCase updateCustomerUseCase(CustomerRepository repository) {
        return new UpdateCustomerUseCase(repository);
    }

    @Bean
    FindCustomerUseCase findCustomerUseCase(CustomerRepository repository) {
        return new FindCustomerUseCase(repository);
    }

    @Bean
    ListCustomersUseCase listCustomersUseCase(CustomerRepository repository) {
        return new ListCustomersUseCase(repository);
    }

    @Bean
    DeleteCustomerUseCase deleteCustomerUseCase(CustomerRepository repository) {
        return new DeleteCustomerUseCase(repository);
    }

    @Bean
    CreateVehicleUseCase createVehicleUseCase(
            VehicleRepository vehicleRepository, CustomerRepository customerRepository) {
        return new CreateVehicleUseCase(vehicleRepository, customerRepository);
    }

    @Bean
    UpdateVehicleUseCase updateVehicleUseCase(VehicleRepository repository) {
        return new UpdateVehicleUseCase(repository);
    }

    @Bean
    FindVehicleUseCase findVehicleUseCase(VehicleRepository repository) {
        return new FindVehicleUseCase(repository);
    }

    @Bean
    ListVehiclesUseCase listVehiclesUseCase(VehicleRepository repository) {
        return new ListVehiclesUseCase(repository);
    }

    @Bean
    DeleteVehicleUseCase deleteVehicleUseCase(VehicleRepository repository) {
        return new DeleteVehicleUseCase(repository);
    }

    @Bean
    ListVehiclesByCustomerUseCase listVehiclesByCustomerUseCase(
            VehicleRepository vehicleRepository, CustomerRepository customerRepository) {
        return new ListVehiclesByCustomerUseCase(vehicleRepository, customerRepository);
    }

    @Bean
    CreateWorkshopServiceUseCase createWorkshopServiceUseCase(WorkshopServiceRepository repository) {
        return new CreateWorkshopServiceUseCase(repository);
    }

    @Bean
    UpdateWorkshopServiceUseCase updateWorkshopServiceUseCase(WorkshopServiceRepository repository) {
        return new UpdateWorkshopServiceUseCase(repository);
    }

    @Bean
    FindWorkshopServiceUseCase findWorkshopServiceUseCase(WorkshopServiceRepository repository) {
        return new FindWorkshopServiceUseCase(repository);
    }

    @Bean
    ListWorkshopServicesUseCase listWorkshopServicesUseCase(WorkshopServiceRepository repository) {
        return new ListWorkshopServicesUseCase(repository);
    }

    @Bean
    DeleteWorkshopServiceUseCase deleteWorkshopServiceUseCase(WorkshopServiceRepository repository) {
        return new DeleteWorkshopServiceUseCase(repository);
    }

    @Bean
    CreatePartUseCase createPartUseCase(PartRepository repository) {
        return new CreatePartUseCase(repository);
    }

    @Bean
    UpdatePartUseCase updatePartUseCase(PartRepository repository) {
        return new UpdatePartUseCase(repository);
    }

    @Bean
    FindPartUseCase findPartUseCase(PartRepository repository) {
        return new FindPartUseCase(repository);
    }

    @Bean
    ListPartsUseCase listPartsUseCase(PartRepository repository) {
        return new ListPartsUseCase(repository);
    }

    @Bean
    DeletePartUseCase deletePartUseCase(PartRepository repository) {
        return new DeletePartUseCase(repository);
    }

    @Bean
    UpdatePartStockUseCase updatePartStockUseCase(PartRepository repository) {
        return new UpdatePartStockUseCase(repository);
    }

    @Bean
    CreateServiceOrderUseCase createServiceOrderUseCase(
            ServiceOrderRepository serviceOrderRepository,
            CustomerRepository customerRepository,
            VehicleRepository vehicleRepository) {
        return new CreateServiceOrderUseCase(
                serviceOrderRepository, customerRepository, vehicleRepository);
    }

    @Bean
    FindServiceOrderUseCase findServiceOrderUseCase(ServiceOrderRepository repository) {
        return new FindServiceOrderUseCase(repository);
    }

    @Bean
    ListServiceOrdersUseCase listServiceOrdersUseCase(ServiceOrderRepository repository) {
        return new ListServiceOrdersUseCase(repository);
    }

    @Bean
    AddServiceToServiceOrderUseCase addServiceToServiceOrderUseCase(
            ServiceOrderRepository serviceOrderRepository,
            WorkshopServiceRepository workshopServiceRepository) {
        return new AddServiceToServiceOrderUseCase(serviceOrderRepository, workshopServiceRepository);
    }

    @Bean
    AddPartToServiceOrderUseCase addPartToServiceOrderUseCase(
            ServiceOrderRepository serviceOrderRepository, PartRepository partRepository) {
        return new AddPartToServiceOrderUseCase(serviceOrderRepository, partRepository);
    }

    @Bean
    GenerateServiceOrderBudgetUseCase generateServiceOrderBudgetUseCase(
            ServiceOrderRepository repository) {
        return new GenerateServiceOrderBudgetUseCase(repository);
    }

    @Bean
    ApproveServiceOrderBudgetUseCase approveServiceOrderBudgetUseCase(
            ServiceOrderRepository repository) {
        return new ApproveServiceOrderBudgetUseCase(repository);
    }

    @Bean
    UpdateServiceOrderStatusUseCase updateServiceOrderStatusUseCase(
            ServiceOrderRepository repository) {
        return new UpdateServiceOrderStatusUseCase(repository);
    }

    @Bean
    ListServiceOrdersByCustomerUseCase listServiceOrdersByCustomerUseCase(
            ServiceOrderRepository serviceOrderRepository, CustomerRepository customerRepository) {
        return new ListServiceOrdersByCustomerUseCase(serviceOrderRepository, customerRepository);
    }

    @Bean
    GetAverageServiceOrderExecutionTimeUseCase getAverageServiceOrderExecutionTimeUseCase(
            ServiceOrderRepository serviceOrderRepository) {
        return new GetAverageServiceOrderExecutionTimeUseCase(serviceOrderRepository);
    }
}
