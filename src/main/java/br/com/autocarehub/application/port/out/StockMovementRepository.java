package br.com.autocarehub.application.port.out;

import java.math.BigDecimal;
import java.util.UUID;

public interface StockMovementRepository {

  void register(
      UUID partId,
      String movementType,
      int quantity,
      BigDecimal unitCost,
      BigDecimal unitPrice,
      String reason);
}
