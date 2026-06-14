package br.com.autocarehub.domain.model;

import br.com.autocarehub.domain.exception.DomainException;
import br.com.autocarehub.domain.valueobject.Money;
import java.util.List;
import java.util.Objects;

public class Budget {

  private final List<BudgetItem> items;
  private final Money totalAmount;

  public Budget(List<BudgetItem> items) {
    this.items = List.copyOf(Objects.requireNonNull(items, "items are required"));
    if (this.items.isEmpty()) {
      throw new DomainException("Budget requires at least one item");
    }
    this.totalAmount =
        this.items.stream().map(BudgetItem::totalPrice).reduce(Money.zero(), Money::add);
  }

  public List<BudgetItem> items() {
    return items;
  }

  public Money totalAmount() {
    return totalAmount;
  }
}
