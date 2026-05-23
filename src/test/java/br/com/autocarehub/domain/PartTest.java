package br.com.autocarehub.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PartTest {

    private static Part part() {
        return new Part("Oil filter", "OIL-001", "Filters", null, "Bosch", Money.of("50.00"), 10, 2);
    }

    @Test
    void shouldNotAllowNegativeStock() {
        assertThatThrownBy(
                () ->
                        new Part(
                                "Oil filter", "OIL-001", "Filters", null, "Bosch", Money.of("50.00"), -1, 2))
                .isInstanceOf(DomainException.class)
                .hasMessage("Stock cannot be negative");
    }

    @Test
    void shouldReduceStockWhenQuantityIsAvailable() {
        Part part = part();

        part.reduceStock(3);

        assertThat(part.stockQuantity()).isEqualTo(7);
    }

    @Test
    void shouldFailWhenReducingMoreThanAvailableStock() {
        Part part = part();

        assertThatThrownBy(() -> part.reduceStock(11))
                .isInstanceOf(DomainException.class)
                .hasMessage("Insufficient stock");
    }

    @Test
    void shouldIncreaseStock() {
        Part part = part();

        part.increaseStock(5);

        assertThat(part.stockQuantity()).isEqualTo(15);
    }

    @Test
    void shouldUpdatePartData() {
        Part part = part();

        part.update("Air filter", "AIR-001", "Filters", "Air", "Mann", Money.of("80.00"), 3);

        assertThat(part.name()).isEqualTo("Air filter");
        assertThat(part.sku()).isEqualTo("AIR-001");
        assertThat(part.category()).isEqualTo("Filters");
        assertThat(part.subcategory()).isEqualTo("Air");
        assertThat(part.brand()).isEqualTo("Mann");
        assertThat(part.unitPrice().value()).isEqualByComparingTo("80.00");
        assertThat(part.minimumStock()).isEqualTo(3);
    }

    @Test
    void shouldReportAvailableStock() {
        Part part = part();

        assertThat(part.hasAvailableStock(10)).isTrue();
        assertThat(part.hasAvailableStock(11)).isFalse();
        assertThat(part.hasAvailableStock(0)).isFalse();
    }

    @Test
    void shouldActivateAndDeactivate() {
        Part part = part();

        part.deactivate();
        assertThat(part.active()).isFalse();

        part.activate();
        assertThat(part.active()).isTrue();
    }

    @Test
    void shouldRejectInvalidStockQuantities() {
        Part part = part();

        assertThatThrownBy(() -> part.increaseStock(0))
                .isInstanceOf(DomainException.class)
                .hasMessage("Quantity must be greater than zero");
        assertThatThrownBy(() -> part.reduceStock(0))
                .isInstanceOf(DomainException.class)
                .hasMessage("Quantity must be greater than zero");
    }

    @Test
    void shouldRejectInvalidPartUpdate() {
        Part part = part();

        assertThatThrownBy(
                () -> part.update("Air filter", "AIR-001", "Filters", null, "Mann", Money.zero(), 3))
                .isInstanceOf(DomainException.class)
                .hasMessage("Unit price must be greater than zero");
        assertThatThrownBy(
                () ->
                        part.update(
                                "Air filter", "AIR-001", "Filters", null, "Mann", Money.of("80.00"), -1))
                .isInstanceOf(DomainException.class)
                .hasMessage("Minimum stock cannot be negative");
    }
}
