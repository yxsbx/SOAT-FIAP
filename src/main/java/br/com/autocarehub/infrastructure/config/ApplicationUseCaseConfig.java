package br.com.autocarehub.infrastructure.config;

import br.com.autocarehub.application.repository.CustomerRepository;
import br.com.autocarehub.application.repository.PartRepository;
import br.com.autocarehub.application.repository.ServiceOrderRepository;
import br.com.autocarehub.application.repository.VehicleRepository;
import br.com.autocarehub.application.repository.WorkshopServiceRepository;
import br.com.autocarehub.application.usecase.auth.LoginUseCase;
import br.com.autocarehub.application.usecase.customer.CreateCustomerUseCase;
import br.com.autocarehub.application.usecase.customer.DeleteCustomerUseCase;
import br.com.autocarehub.application.usecase.customer.FindCustomerUseCase;
import br.com.autocarehub.application.usecase.customer.ListCustomersUseCase;
import br.com.autocarehub.application.usecase.customer.UpdateCustomerUseCase;
import br.com.autocarehub.application.usecase.part.CreatePartUseCase;
import br.com.autocarehub.application.usecase.part.DeletePartUseCase;
import br.com.autocarehub.application.usecase.part.FindPartUseCase;
import br.com.autocarehub.application.usecase.part.ListPartsUseCase;
import br.com.autocarehub.application.usecase.part.UpdatePartStockUseCase;
import br.com.autocarehub.application.usecase.part.UpdatePartUseCase;
import br.com.autocarehub.application.usecase.serviceorder.AddPartToServiceOrderUseCase;
import br.com.autocarehub.application.usecase.serviceorder.AddServiceToServiceOrderUseCase;
import br.com.autocarehub.application.usecase.serviceorder.ApproveServiceOrderBudgetUseCase;
import br.com.autocarehub.application.usecase.serviceorder.CreateServiceOrderUseCase;
import br.com.autocarehub.application.usecase.serviceorder.FindServiceOrderUseCase;
import br.com.autocarehub.application.usecase.serviceorder.GenerateServiceOrderBudgetUseCase;
import br.com.autocarehub.application.usecase.serviceorder.ListServiceOrdersByCustomerUseCase;
import br.com.autocarehub.application.usecase.serviceorder.ListServiceOrdersUseCase;
import br.com.autocarehub.application.usecase.serviceorder.UpdateServiceOrderStatusUseCase;
import br.com.autocarehub.application.usecase.vehicle.CreateVehicleUseCase;
import br.com.autocarehub.application.usecase.vehicle.DeleteVehicleUseCase;
import br.com.autocarehub.application.usecase.vehicle.FindVehicleUseCase;
import br.com.autocarehub.application.usecase.vehicle.ListVehiclesByCustomerUseCase;
import br.com.autocarehub.application.usecase.vehicle.ListVehiclesUseCase;
import br.com.autocarehub.application.usecase.vehicle.UpdateVehicleUseCase;
import br.com.autocarehub.application.usecase.workshopservice.CreateWorkshopServiceUseCase;
import br.com.autocarehub.application.usecase.workshopservice.DeleteWorkshopServiceUseCase;
import br.com.autocarehub.application.usecase.workshopservice.FindWorkshopServiceUseCase;
import br.com.autocarehub.application.usecase.workshopservice.ListWorkshopServicesUseCase;
import br.com.autocarehub.application.usecase.workshopservice.UpdateWorkshopServiceUseCase;
import br.com.autocarehub.infrastructure.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationUseCaseConfig {

    @Bean
    LoginUseCase loginUseCase(AuthenticationManager authenticationManager, JwtService jwtService) {
        return new LoginUseCase(authenticationManager, jwtService);
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
    CreateVehicleUseCase createVehicleUseCase(VehicleRepository vehicleRepository, CustomerRepository customerRepository) {
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
    ListVehiclesByCustomerUseCase listVehiclesByCustomerUseCase(VehicleRepository vehicleRepository, CustomerRepository customerRepository) {
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
    CreateServiceOrderUseCase createServiceOrderUseCase(ServiceOrderRepository serviceOrderRepository, CustomerRepository customerRepository, VehicleRepository vehicleRepository) {
        return new CreateServiceOrderUseCase(serviceOrderRepository, customerRepository, vehicleRepository);
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
    AddServiceToServiceOrderUseCase addServiceToServiceOrderUseCase(ServiceOrderRepository serviceOrderRepository, WorkshopServiceRepository workshopServiceRepository) {
        return new AddServiceToServiceOrderUseCase(serviceOrderRepository, workshopServiceRepository);
    }

    @Bean
    AddPartToServiceOrderUseCase addPartToServiceOrderUseCase(ServiceOrderRepository serviceOrderRepository, PartRepository partRepository) {
        return new AddPartToServiceOrderUseCase(serviceOrderRepository, partRepository);
    }

    @Bean
    GenerateServiceOrderBudgetUseCase generateServiceOrderBudgetUseCase(ServiceOrderRepository repository) {
        return new GenerateServiceOrderBudgetUseCase(repository);
    }

    @Bean
    ApproveServiceOrderBudgetUseCase approveServiceOrderBudgetUseCase(ServiceOrderRepository repository) {
        return new ApproveServiceOrderBudgetUseCase(repository);
    }

    @Bean
    UpdateServiceOrderStatusUseCase updateServiceOrderStatusUseCase(ServiceOrderRepository repository) {
        return new UpdateServiceOrderStatusUseCase(repository);
    }

    @Bean
    ListServiceOrdersByCustomerUseCase listServiceOrdersByCustomerUseCase(ServiceOrderRepository serviceOrderRepository, CustomerRepository customerRepository) {
        return new ListServiceOrdersByCustomerUseCase(serviceOrderRepository, customerRepository);
    }
}
