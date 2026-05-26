package br.com.autocarehub.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PlatformFeePolicy {

    private PlatformFeePolicy() {
    }

    public static Result calculate(BigDecimal monthlyGrossRevenue) {
        BigDecimal gross = monthlyGrossRevenue == null ? BigDecimal.ZERO : monthlyGrossRevenue;
        Tier tier = resolveTier(gross);
        BigDecimal feeAmount = gross.multiply(tier.rate()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal net = gross.subtract(feeAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal nextTierGap =
                tier.nextStartsAt() == null
                        ? BigDecimal.ZERO
                        : tier.nextStartsAt().subtract(gross).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        return new Result(gross, tier.rate(), feeAmount, net, nextTierGap, tier.nextRate());
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

    private record Tier(BigDecimal rate, BigDecimal nextStartsAt, BigDecimal nextRate) {
    }

    public record Result(
            BigDecimal gross,
            BigDecimal feeRate,
            BigDecimal feeAmount,
            BigDecimal netAmount,
            BigDecimal nextTierGap,
            BigDecimal nextTierRate) {
    }
}
