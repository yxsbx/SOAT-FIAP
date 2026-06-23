package br.com.autocarehub.domain.policy;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class PlatformFeePolicyTest {

    @Test
    void appliesTenPercentUntilTenThousand() {
        PlatformFeePolicy.Result result = PlatformFeePolicy.calculate(new BigDecimal("10000.00"));

        assertThat(result.feeRate()).isEqualByComparingTo("0.10");
        assertThat(result.feeAmount()).isEqualByComparingTo("1000.00");
        assertThat(result.netAmount()).isEqualByComparingTo("9000.00");
        assertThat(result.nextTierGap()).isEqualByComparingTo("0.01");
        assertThat(result.nextTierRate()).isEqualByComparingTo("0.08");
    }

    @Test
    void appliesEightPercentAfterTenThousandUntilFifteenThousand() {
        PlatformFeePolicy.Result result = PlatformFeePolicy.calculate(new BigDecimal("10000.01"));

        assertThat(result.feeRate()).isEqualByComparingTo("0.08");
        assertThat(result.nextTierGap()).isEqualByComparingTo("5000.00");
        assertThat(result.nextTierRate()).isEqualByComparingTo("0.07");
    }

    @Test
    void appliesSevenPercentAfterFifteenThousandUntilTwentyThousand() {
        PlatformFeePolicy.Result result = PlatformFeePolicy.calculate(new BigDecimal("15000.01"));

        assertThat(result.feeRate()).isEqualByComparingTo("0.07");
        assertThat(result.nextTierGap()).isEqualByComparingTo("5000.00");
        assertThat(result.nextTierRate()).isEqualByComparingTo("0.05");
    }

    @Test
    void appliesFivePercentAboveTwentyThousand() {
        PlatformFeePolicy.Result result = PlatformFeePolicy.calculate(new BigDecimal("20000.01"));

        assertThat(result.feeRate()).isEqualByComparingTo("0.05");
        assertThat(result.nextTierGap()).isEqualByComparingTo("0.00");
        assertThat(result.nextTierRate()).isNull();
    }
}
