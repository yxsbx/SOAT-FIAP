package br.com.autocarehub.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.autocarehub.domain.enums.StockMovementType;
import br.com.autocarehub.domain.exception.DomainException;
import br.com.autocarehub.domain.service.DomainValidation;
import br.com.autocarehub.domain.valueobject.Address;
import br.com.autocarehub.domain.valueobject.Money;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DomainAdditionalCoverageTest {

  @Test
  void shouldCoverMoneyOperationsAndValidation() {
    Money money = Money.of(new BigDecimal("10.555"));

    assertThat(money.value()).isEqualByComparingTo("10.56");
    assertThat(Money.zero().isZero()).isTrue();
    assertThat(Money.zero().isZeroOrNegative()).isTrue();
    assertThat(money.add(Money.of("2.44")).value()).isEqualByComparingTo("13.00");
    assertThat(money.multiply(3).value()).isEqualByComparingTo("31.68");
    assertThat(money.compareTo(Money.of("9.00"))).isPositive();

    assertThatThrownBy(() -> Money.of("-1.00"))
        .isInstanceOf(DomainException.class)
        .hasMessage("Money cannot be negative");
    assertThatThrownBy(() -> money.multiply(-1))
        .isInstanceOf(DomainException.class)
        .hasMessage("Quantity cannot be negative");
  }

  @Test
  void shouldCoverStockMovementValidationAndEnumValues() {
    UUID partId = UUID.randomUUID();
    StockMovement movement =
        new StockMovement(
            partId,
            StockMovementType.ENTRY,
            2,
            Money.of("10.00"),
            Money.of("15.00"),
            " Compra ",
            LocalDateTime.now());

    assertThat(movement.partId()).isEqualTo(partId);
    assertThat(movement.type()).isEqualTo(StockMovementType.ENTRY);
    assertThat(movement.reason()).isEqualTo("Compra");
    assertThat(StockMovementType.valueOf("EXIT")).isEqualTo(StockMovementType.EXIT);
    assertThat(StockMovementType.values()).contains(StockMovementType.SALE);

    assertThatThrownBy(
            () ->
                new StockMovement(
                    partId,
                    StockMovementType.SALE,
                    0,
                    Money.of("10.00"),
                    Money.of("15.00"),
                    "Venda",
                    LocalDateTime.now()))
        .isInstanceOf(DomainException.class)
        .hasMessage("Quantity must be greater than zero");
  }

  @Test
  void shouldCoverAdditionalPartBranches() {
    Part part =
        new Part(
            "Filtro de oleo",
            "Filtro de oleo do motor",
            "OIL-EXTRA",
            "Filtros",
            "Oleo",
            "Bosch",
            Money.of("25.00"),
            Money.of("50.00"),
            4,
            4);

    assertThat(part.stockStatus()).isEqualTo("LOW_STOCK");

    part.reserveStock(2);
    assertThat(part.stockStatus()).isEqualTo("LOW_STOCK");

    part.releaseReservedStock(99);
    assertThat(part.reservedQuantity()).isZero();
    assertThat(part.reservationExpiresAt()).isNull();

    part.reduceStock(4);
    assertThat(part.stockStatus()).isEqualTo("OUT_OF_STOCK");

    part.deactivate();
    assertThat(part.stockStatus()).isEqualTo("INACTIVE");
    assertThat(part.costPrice().value()).isEqualByComparingTo("25.00");
    assertThat(part.subcategory()).isEqualTo("Oleo");
    assertThat(part.description()).isEqualTo("Filtro de oleo do motor");
  }

  @Test
  void shouldCoverDomainValidationAndAddressBranches() {
    assertThat(DomainValidation.optionalText(null)).isNull();
    assertThat(DomainValidation.optionalText("  valor  ")).isEqualTo("valor");
    assertThat(DomainValidation.requireText(" texto ", "required", 10)).isEqualTo("texto");
    assertThatThrownBy(() -> DomainValidation.requireText(" ", "required", 10))
        .isInstanceOf(DomainException.class)
        .hasMessage("required");
    assertThatThrownBy(() -> DomainValidation.requireText("texto longo", "required", 3))
        .isInstanceOf(DomainException.class)
        .hasMessage("Text exceeds maximum length");

    Address address =
        new Address("Rua A", "10", "Apto 1", "Centro", "São Paulo", "SP", "01001-000");

    assertThat(address.complement()).isEqualTo("Apto 1");
    assertThat(address.zipCode()).isEqualTo("01001000");
  }
}
