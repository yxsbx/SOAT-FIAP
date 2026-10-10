package br.com.autocarehub.interfaces.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.autocarehub.application.port.out.PartRepository;
import br.com.autocarehub.application.port.out.ServiceOrderRepository;
import br.com.autocarehub.application.usecase.serviceorder.ApproveServiceOrderBudgetUseCase;
import br.com.autocarehub.application.usecase.serviceorder.DecideServiceOrderBudgetUseCase;
import br.com.autocarehub.application.usecase.serviceorder.GenerateServiceOrderBudgetUseCase;
import br.com.autocarehub.application.usecase.serviceorder.ListServiceOrdersUseCase;
import br.com.autocarehub.domain.enums.ServiceOrderStatus;
import br.com.autocarehub.domain.exception.DomainException;
import br.com.autocarehub.domain.model.Part;
import br.com.autocarehub.domain.model.ServiceOrder;
import br.com.autocarehub.domain.valueobject.Money;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@AutoConfigureMockMvc
class BudgetConsistencyIntegrationTest {
    private static final UUID CUSTOMER = UUID.fromString("10000000-0000-0000-0000-000000000001");
    private static final UUID VEHICLE = UUID.fromString("20000000-0000-0000-0000-000000000001");

    @Autowired
    ServiceOrderRepository orders;

    @Autowired
    PartRepository parts;

    @Autowired
    GenerateServiceOrderBudgetUseCase generate;

    @Autowired
    ApproveServiceOrderBudgetUseCase approve;

    @Autowired
    DecideServiceOrderBudgetUseCase decide;

    @Autowired
    ListServiceOrdersUseCase list;

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Test
    void concurrentOrdersSharingPartDoNotLoseStockUpdates() throws Exception {
        Part part = part();
        UUID first = budget(part);
        UUID second = budget(parts.findById(part.id()).orElseThrow());
        simultaneously(() -> approve.execute(first), () -> approve.execute(second));
        assertThat(parts.findById(part.id()).orElseThrow().stockQuantity()).isEqualTo(6);
        assertThat(parts.findById(part.id()).orElseThrow().reservedQuantity()).isZero();
    }

    @Test
    void rejectionReplaySurvivesReloadAndReleasesStockOnlyOnce() {
        Part part = part();
        UUID id = budget(part);
        ServiceOrder first = reject(id);
        ServiceOrder replay = reject(id);
        assertThat(replay.rejectedAt()).isEqualTo(first.rejectedAt());
        assertThat(replay.status()).isEqualTo(ServiceOrderStatus.EM_DIAGNOSTICO);
        assertThat(parts.findById(part.id()).orElseThrow().reservedQuantity()).isZero();
        assertThat(parts.findById(part.id()).orElseThrow().stockQuantity()).isEqualTo(10);
        assertThatThrownBy(() -> approve.execute(id)).isInstanceOf(DomainException.class);

        generate.execute(id);
        assertThat(orders.findById(id).orElseThrow().rejectedAt()).isNull();
        approve.execute(id);
        assertThat(parts.findById(part.id()).orElseThrow().stockQuantity()).isEqualTo(8);
    }

    @Test
    void approvalReplayKeepsTimestampAndCannotBeReversedByRejection() {
        Part part = part();
        UUID id = budget(part);
        ServiceOrder first = approve.execute(id);
        assertThat(approve.execute(id).approvedAt()).isEqualTo(first.approvedAt());
        assertThatThrownBy(() -> reject(id))
                .isInstanceOf(DomainException.class)
                .hasMessage("Approved budget cannot be rejected");
        assertThat(orders.findById(id).orElseThrow().approvedAt()).isEqualTo(first.approvedAt());
        assertThat(parts.findById(part.id()).orElseThrow().stockQuantity()).isEqualTo(8);
    }

    @Test
    void concurrentApprovalsConsumeStockOnce() throws Exception {
        Part part = part();
        UUID id = budget(part);
        List<ServiceOrder> responses = simultaneously(() -> approve.execute(id), () -> approve.execute(id));
        assertThat(responses.get(0).approvedAt()).isEqualTo(responses.get(1).approvedAt());
        assertThat(parts.findById(part.id()).orElseThrow().stockQuantity()).isEqualTo(8);
        assertThat(parts.findById(part.id()).orElseThrow().reservedQuantity()).isZero();
    }

    @Test
    void concurrentRejectionsReleaseStockOnce() throws Exception {
        Part part = part();
        UUID id = budget(part);
        List<ServiceOrder> responses = simultaneously(() -> reject(id), () -> reject(id));
        assertThat(responses.get(0).rejectedAt()).isEqualTo(responses.get(1).rejectedAt());
        assertThat(parts.findById(part.id()).orElseThrow().stockQuantity()).isEqualTo(10);
        assertThat(parts.findById(part.id()).orElseThrow().reservedQuantity()).isZero();
    }

    @Test
    void failureOnSecondPartRollsBackFirstPartAndApproval() {
        List<Part> stock = List.of(part(), part()).stream()
                .sorted(java.util.Comparator.comparing(Part::id))
                .toList();
        UUID id = budget(stock.toArray(Part[]::new));
        jdbc.update(
                "update parts set reserved_quantity = 0, stock_quantity = 0 where id = ?",
                stock.get(1).id());
        assertThatThrownBy(() -> approve.execute(id)).isInstanceOf(DomainException.class);
        assertThat(orders.findById(id).orElseThrow().approvedAt()).isNull();
        assertThat(parts.findById(stock.get(0).id()).orElseThrow().stockQuantity())
                .isEqualTo(10);
        assertThat(parts.findById(stock.get(0).id()).orElseThrow().reservedQuantity())
                .isEqualTo(2);
    }

    @Test
    void queueExcludesClosedOrdersEvenWithExplicitFilterAndPreservesHistory() {
        LocalDateTime date = LocalDateTime.of(2090, 1, 1, 0, 0);
        ServiceOrder received = stored(ServiceOrderStatus.RECEBIDA, date);
        ServiceOrder newer = stored(ServiceOrderStatus.EM_EXECUCAO, date.plusDays(2));
        ServiceOrder older = stored(ServiceOrderStatus.EM_EXECUCAO, date.plusDays(1));
        ServiceOrder finished = stored(ServiceOrderStatus.FINALIZADA, date);
        ServiceOrder delivered = stored(ServiceOrderStatus.ENTREGUE, date);
        var query = new ListServiceOrdersUseCase.Query(null, CUSTOMER, VEHICLE, date, date.plusDays(3), 0, 2);
        assertThat(list.execute(query)).extracting(ServiceOrder::id).containsExactly(older.id(), newer.id());
        assertThat(list.execute(
                        new ListServiceOrdersUseCase.Query(null, CUSTOMER, VEHICLE, date, date.plusDays(3), 1, 2)))
                .extracting(ServiceOrder::id)
                .containsExactly(received.id());
        for (ServiceOrder closed : List.of(finished, delivered)) {
            assertThat(list.execute(new ListServiceOrdersUseCase.Query(
                            closed.status(), CUSTOMER, VEHICLE, date, date.plusDays(3))))
                    .isEmpty();
            assertThat(orders.findById(closed.id())).isPresent();
            assertThat(orders.findByCustomerId(CUSTOMER))
                    .extracting(ServiceOrder::id)
                    .contains(closed.id());
        }
    }

    @Test
    void readOnlyTransactionCanReadOrdersAndParts() {
        Part part = part();
        UUID id = budget(part);
        var transaction = new TransactionTemplate(transactionManager);
        transaction.setReadOnly(true);
        transaction.executeWithoutResult(status -> {
            assertThat(orders.findById(id)).isPresent();
            assertThat(parts.findById(part.id())).isPresent();
        });
    }

    private ServiceOrder stored(ServiceOrderStatus status, LocalDateTime date) {
        return orders.save(new ServiceOrder(
                UUID.randomUUID(),
                CUSTOMER,
                VEHICLE,
                status,
                "Queue regression",
                List.of(),
                List.of(),
                Money.zero(),
                date,
                null,
                null,
                null,
                null,
                null));
    }

    private Part part() {
        return parts.save(Part.create(
                new Part.CatalogData("Filter", "Filter", UUID.randomUUID().toString(), "Filters", "Oil", "Brand"),
                new Part.Pricing(Money.of("10"), Money.of("20")),
                10,
                1));
    }

    private UUID budget(Part... stock) {
        ServiceOrder order = new ServiceOrder(CUSTOMER, VEHICLE, "Budget regression");
        for (Part part : stock) {
            order.addPart(part, 2);
        }
        orders.save(order);
        generate.execute(order.id());
        return order.id();
    }

    private ServiceOrder reject(UUID id) {
        return decide.execute(new DecideServiceOrderBudgetUseCase.Command(
                id, DecideServiceOrderBudgetUseCase.Decision.REJECTED, "test", "Review"));
    }

    private List<ServiceOrder> simultaneously(Callable<ServiceOrder> first, Callable<ServiceOrder> second)
            throws Exception {
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var a = executor.submit(() -> {
                ready.countDown();
                start.await();
                return first.call();
            });
            var b = executor.submit(() -> {
                ready.countDown();
                start.await();
                return second.call();
            });
            try {
                assertThat(ready.await(10, TimeUnit.SECONDS)).isTrue();
            } finally {
                start.countDown();
            }
            return List.of(a.get(15, TimeUnit.SECONDS), b.get(15, TimeUnit.SECONDS));
        }
    }
}
