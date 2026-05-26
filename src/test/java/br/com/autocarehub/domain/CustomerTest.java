package br.com.autocarehub.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class CustomerTest {

  private static Customer customer() {
    return new Customer(
        "Maria Silva", Document.from("52998224725"), "11999999999", "maria@example.com", null);
  }

  @Test
  void shouldValidateDocument() {
    Customer customer =
        new Customer(
            "Maria Silva", Document.from("52998224725"), "11999999999", "maria@example.com", null);

    assertThat(customer.document().type()).isEqualTo(DocumentType.CPF);
    assertThat(customer.document().value()).isEqualTo("52998224725");
  }

  @Test
  void shouldRejectInvalidDocument() {
    assertThatThrownBy(
            () ->
                new Customer(
                    "Maria Silva",
                    Document.from("11111111111"),
                    "11999999999",
                    "maria@example.com",
                    null))
        .isInstanceOf(DomainException.class)
        .hasMessage("Invalid document");
  }

  @Test
  void shouldValidateCnpjDocument() {
    Document document = Document.from("11222333000181");

    assertThat(document.type()).isEqualTo(DocumentType.CNPJ);
    assertThat(document.value()).isEqualTo("11222333000181");
  }

  @Test
  void shouldUpdateCustomerData() {
    Customer customer = customer();
    Address address = new Address("Rua A", "10", null, "Centro", "Sao Paulo", "SP", "01001000");

    customer.rename("Maria Souza");
    customer.updateContact("11888888888", "souza@example.com");
    customer.updateAddress(address);

    assertThat(customer.name()).isEqualTo("Maria Souza");
    assertThat(customer.phone()).isEqualTo("11888888888");
    assertThat(customer.email()).isEqualTo("souza@example.com");
    assertThat(customer.address()).isEqualTo(address);
  }

  @Test
  void shouldActivateAndDeactivate() {
    Customer customer = customer();

    customer.deactivate();
    assertThat(customer.active()).isFalse();

    customer.activate();
    assertThat(customer.active()).isTrue();
  }

  @Test
  void shouldRejectInvalidCustomerData() {
    Customer customer = customer();

    assertThatThrownBy(() -> customer.rename(" "))
        .isInstanceOf(DomainException.class)
        .hasMessage("Name is required");
    assertThatThrownBy(() -> customer.updateContact("11999999999", "invalid-email"))
        .isInstanceOf(DomainException.class)
        .hasMessage("Invalid email");
    assertThatThrownBy(() -> Document.from("123"))
        .isInstanceOf(DomainException.class)
        .hasMessage("Document must be CPF or CNPJ");
  }
}
