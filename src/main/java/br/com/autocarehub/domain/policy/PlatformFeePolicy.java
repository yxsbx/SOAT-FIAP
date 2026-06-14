package br.com.autocarehub.domain.policy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.jspecify.annotations.Nullable;

public final class PlatformFeePolicy {

  private PlatformFeePolicy() {}

  public static Result calculate(BigDecimal monthlyGrossRevenue) {
    Tier tier = resolveTier(monthlyGrossRevenue);
    BigDecimal feeAmount =
        monthlyGrossRevenue.multiply(tier.rate()).setScale(2, RoundingMode.HALF_UP);
    BigDecimal net = monthlyGrossRevenue.subtract(feeAmount).setScale(2, RoundingMode.HALF_UP);
    BigDecimal nextTierGap =
        tier.nextStartsAt() == null
            ? BigDecimal.ZERO
            : tier.nextStartsAt()
                .subtract(monthlyGrossRevenue)
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    return new Result(
        monthlyGrossRevenue, tier.rate(), feeAmount, net, nextTierGap, tier.nextRate());
  }

  private static Tier resolveTier(BigDecimal gross) {
    if (gross.compareTo(new BigDecimal("10000.00")) <= 0) {
      return new Tier(new BigDecimal("0.10"), new BigDecimal("10000.01"), new BigDecimal("0.08"));
    }
    if (gross.compareTo(new BigDecimal("15000.00")) <= 0) {
      return new Tier(new BigDecimal("0.08"), new BigDecimal("15000.01"), new BigDecimal("0.07"));
    }
    if (gross.compareTo(new BigDecimal("20000.00")) <= 0) {
      return new Tier(new BigDecimal("0.07"), new BigDecimal("20000.01"), new BigDecimal("0.05"));
    }
    return new Tier(new BigDecimal("0.05"), null, null);
  }

  private record Tier(
      BigDecimal rate, @Nullable BigDecimal nextStartsAt, @Nullable BigDecimal nextRate) {}

  public record Result(
      BigDecimal gross,
      BigDecimal feeRate,
      BigDecimal feeAmount,
      BigDecimal netAmount,
      BigDecimal nextTierGap,
      @Nullable BigDecimal nextTierRate) {}
}
