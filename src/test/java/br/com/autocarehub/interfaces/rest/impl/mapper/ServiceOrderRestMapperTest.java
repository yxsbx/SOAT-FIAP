package br.com.autocarehub.interfaces.rest.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.autocarehub.application.usecase.serviceorder.TrackServiceOrderUseCase;
import br.com.autocarehub.domain.valueobject.Address;
import br.com.autocarehub.domain.model.Customer;
import br.com.autocarehub.domain.valueobject.Document;
import br.com.autocarehub.domain.valueobject.Money;
import br.com.autocarehub.domain.model.Part;
import br.com.autocarehub.domain.valueobject.Plate;
import br.com.autocarehub.domain.model.ServiceOrder;
import br.com.autocarehub.domain.model.Vehicle;
import br.com.autocarehub.domain.model.WorkshopService;
import br.com.autocarehub.interfaces.rest.generated.model.ServiceOrderStatusHistoryItem;
import br.com.autocarehub.interfaces.rest.generated.model.ServiceOrderTrackingResponse;
import java.util.List;
import org.junit.jupiter.api.Test;

class ServiceOrderRestMapperTest {

  @Test
  void shouldExposePendingBudgetAndOnlyExistingStatusEventsOnTrackingResponse() {
    Customer customer = customer();
    Vehicle vehicle = vehicle(customer);
    ServiceOrder serviceOrder =
        new ServiceOrder(customer.id(), vehicle.id(), "Cliente relata barulho no motor");

    ServiceOrderTrackingResponse response = trackingResponse(serviceOrder, customer, vehicle);

    assertThat(response.getBudget().getGenerated()).isFalse();
    assertThat(response.getBudget().getApproved()).isFalse();
    assertThat(response.getBudget().getGeneratedAt()).isNull();
    assertThat(response.getBudget().getApprovedAt()).isNull();
    assertThat(response.getStatusHistory()).hasSize(1);
    assertThat(response.getStatusHistory().get(0).getOccurredAt()).isNotNull();
    assertThat(response.getStatusHistory().get(0).getDescription())
        .isEqualTo("Ordem de serviço criada");
  }

  @Test
  void shouldExposeBudgetApprovalAndExecutionEventsWhenTheyHappened() {
    Customer customer = customer();
    Vehicle vehicle = vehicle(customer);
    ServiceOrder serviceOrder =
        new ServiceOrder(customer.id(), vehicle.id(), "Cliente relata vazamento de óleo");
    serviceOrder.addService(
        new WorkshopService(
            "Troca de óleo", "Substituição de óleo e filtro", Money.of("120.00"), 60),
        1);
    serviceOrder.addPart(
        new Part(
            "Filtro de óleo",
            "Filtro de óleo do motor",
            "OIL-MAP-001",
            "Filtros",
            "Óleo",
            "Bosch",
            Money.of("30.00"),
            Money.of("60.00"),
            10,
            2),
        1);
    serviceOrder.generateBudget();
    serviceOrder.approveBudget();
    serviceOrder.startExecution();
    serviceOrder.finish();

    ServiceOrderTrackingResponse response = trackingResponse(serviceOrder, customer, vehicle);

    assertThat(response.getBudget().getGenerated()).isTrue();
    assertThat(response.getBudget().getApproved()).isTrue();
    assertThat(response.getBudget().getGeneratedAt()).isNotNull();
    assertThat(response.getBudget().getApprovedAt()).isNotNull();
    assertThat(response.getStatusHistory())
        .extracting(ServiceOrderStatusHistoryItem::getDescription)
        .containsExactly(
            "Ordem de serviço criada",
            "Orçamento gerado e disponibilizado para aprovação",
            "Orçamento aprovado pelo cliente",
            "Execução iniciada",
            "Serviço finalizado");
    assertThat(response.getStatusHistory())
        .allSatisfy(historyItem -> assertThat(historyItem.getOccurredAt()).isNotNull());
  }

  private static ServiceOrderTrackingResponse trackingResponse(
      ServiceOrder serviceOrder, Customer customer, Vehicle vehicle) {
    return ServiceOrderRestMapper.toTrackingListResponse(
            List.of(new TrackServiceOrderUseCase.Output(serviceOrder, customer, vehicle)))
        .getItems()
        .get(0);
  }

  private static Customer customer() {
    return new Customer(
        "Maria Silva", Document.from("52998224725"), "11999999999", "maria@example.com", address());
  }

  private static Vehicle vehicle(Customer customer) {
    return new Vehicle(customer.id(), new Plate("ABC1D23"), "Honda", "Civic", 2020, 35000);
  }

  private static Address address() {
    return new Address(
        "Avenida Paulista", "1000", null, "Bela Vista", "São Paulo", "SP", "01310-100");
  }
}
