package br.com.autocarehub.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceOrderTest {

    private static ServiceOrder serviceOrderWithItems() {
        ServiceOrder serviceOrder = serviceOrder();
        serviceOrder.addService(
                new WorkshopService("Oil change", "Oil and filter replacement", Money.of("100.00"), 60), 2);
        serviceOrder.addPart(
                new Part("Oil filter", "OIL-001", "Filters", null, "Bosch", Money.of("50.00"), 10, 2), 4);
        return serviceOrder;
    }

    private static ServiceOrder serviceOrder() {
        return new ServiceOrder(
                java.util.UUID.randomUUID(), java.util.UUID.randomUUID(), "Initial diagnostic notes");
    }

    @Test
    void shouldStartWithReceivedStatus() {
        ServiceOrder serviceOrder = serviceOrder();

        assertThat(serviceOrder.status()).isEqualTo(ServiceOrderStatus.RECEBIDA);
    }

    @Test
    void shouldStartDiagnosisCorrectly() {
        ServiceOrder serviceOrder = serviceOrder();

        serviceOrder.startDiagnosis();

        assertThat(serviceOrder.status()).isEqualTo(ServiceOrderStatus.EM_DIAGNOSTICO);
    }

    @Test
    void shouldGenerateBudgetWithServicesAndParts() {
        ServiceOrder serviceOrder = serviceOrderWithItems();

        Money total = serviceOrder.generateBudget();

        assertThat(total.value()).isEqualByComparingTo("400.00");
        assertThat(serviceOrder.totalAmount().value()).isEqualByComparingTo("400.00");
    }

    @Test
    void shouldChangeToWaitingApprovalAfterGeneratingBudget() {
        ServiceOrder serviceOrder = serviceOrderWithItems();

        serviceOrder.generateBudget();

        assertThat(serviceOrder.status()).isEqualTo(ServiceOrderStatus.AGUARDANDO_APROVACAO);
        assertThat(serviceOrder.budgetGeneratedAt()).isNotNull();
    }

    @Test
    void shouldApproveBudget() {
        ServiceOrder serviceOrder = serviceOrderWithItems();
        serviceOrder.generateBudget();

        serviceOrder.approveBudget();

        assertThat(serviceOrder.approvedAt()).isNotNull();
    }

    @Test
    void shouldNotStartExecutionWithoutApproval() {
        ServiceOrder serviceOrder = serviceOrderWithItems();
        serviceOrder.generateBudget();

        assertThatThrownBy(serviceOrder::startExecution)
                .isInstanceOf(DomainException.class)
                .hasMessage("Execution cannot start without budget approval");
    }

    @Test
    void shouldNotFinishWithoutBeingInProgress() {
        ServiceOrder serviceOrder = serviceOrderWithItems();
        serviceOrder.generateBudget();
        serviceOrder.approveBudget();

        assertThatThrownBy(serviceOrder::finish)
                .isInstanceOf(DomainException.class)
                .hasMessage("Service order can only be finished while in progress");
    }

    @Test
    void shouldNotDeliverWithoutBeingFinished() {
        ServiceOrder serviceOrder = serviceOrderWithItems();
        serviceOrder.generateBudget();
        serviceOrder.approveBudget();
        serviceOrder.startExecution();

        assertThatThrownBy(serviceOrder::deliver)
                .isInstanceOf(DomainException.class)
                .hasMessage("Service order can only be delivered after finished");
    }

    @Test
    void shouldFollowCompleteStatusFlow() {
        ServiceOrder serviceOrder = serviceOrderWithItems();

        serviceOrder.startDiagnosis();
        serviceOrder.generateBudget();
        serviceOrder.approveBudget();
        serviceOrder.startExecution();
        serviceOrder.finish();
        serviceOrder.deliver();

        assertThat(serviceOrder.status()).isEqualTo(ServiceOrderStatus.ENTREGUE);
        assertThat(serviceOrder.budgetGeneratedAt()).isNotNull();
        assertThat(serviceOrder.approvedAt()).isNotNull();
        assertThat(serviceOrder.startedAt()).isNotNull();
        assertThat(serviceOrder.finishedAt()).isNotNull();
        assertThat(serviceOrder.deliveredAt()).isNotNull();
    }
}
