package br.com.autocarehub.domain.policy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.jspecify.annotations.Nullable;

public final class PlatformFeePolicy {

    private static final BigDecimal LOW_TIER_LIMIT = new BigDecimal("10000.00");
    private static final BigDecimal MIDDLE_TIER_LIMIT = new BigDecimal("15000.00");
    private static final BigDecimal HIGH_TIER_LIMIT = new BigDecimal("20000.00");
    private static final BigDecimal LOW_RATE = new BigDecimal("0.10");
    private static final BigDecimal MIDDLE_RATE = new BigDecimal("0.08");
    private static final BigDecimal HIGH_RATE = new BigDecimal("0.07");
    private static final BigDecimal ENTERPRISE_RATE = new BigDecimal("0.05");
    private static final BigDecimal MIDDLE_TIER_START = new BigDecimal("10000.01");
    private static final BigDecimal HIGH_TIER_START = new BigDecimal("15000.01");
    private static final BigDecimal ENTERPRISE_TIER_START = new BigDecimal("20000.01");

    private PlatformFeePolicy() {}

    public static Result calculate(BigDecimal monthlyGrossRevenue) {
        Tier tier = resolveTier(monthlyGrossRevenue);
        BigDecimal feeAmount = monthlyGrossRevenue.multiply(tier.rate()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal net = monthlyGrossRevenue.subtract(feeAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal nextTierGap = tier.nextStartsAt() == null
                ? BigDecimal.ZERO
                : tier.nextStartsAt()
                        .subtract(monthlyGrossRevenue)
                        .max(BigDecimal.ZERO)
                        .setScale(2, RoundingMode.HALF_UP);
        return new Result(monthlyGrossRevenue, tier.rate(), feeAmount, net, nextTierGap, tier.nextRate());
    }

    private static Tier resolveTier(BigDecimal gross) {
        if (gross.compareTo(LOW_TIER_LIMIT) <= 0) {
            return new Tier(LOW_RATE, MIDDLE_TIER_START, MIDDLE_RATE);
        }
        if (gross.compareTo(MIDDLE_TIER_LIMIT) <= 0) {
            return new Tier(MIDDLE_RATE, HIGH_TIER_START, HIGH_RATE);
        }
        if (gross.compareTo(HIGH_TIER_LIMIT) <= 0) {
            return new Tier(HIGH_RATE, ENTERPRISE_TIER_START, ENTERPRISE_RATE);
        }
        return new Tier(ENTERPRISE_RATE, null, null);
    }

    private record Tier(BigDecimal rate, @Nullable BigDecimal nextStartsAt, @Nullable BigDecimal nextRate) {}

    public record Result(
            BigDecimal gross,
            BigDecimal feeRate,
            BigDecimal feeAmount,
            BigDecimal netAmount,
            BigDecimal nextTierGap,
            @Nullable BigDecimal nextTierRate) {}
}
