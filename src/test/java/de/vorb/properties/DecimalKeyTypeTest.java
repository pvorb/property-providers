package de.vorb.properties;

import static de.vorb.properties.KeyTypes.DECIMAL;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Test;

import com.google.common.truth.Truth;

public class DecimalKeyTypeTest {

    @Test
    public void testParseZero() {
        Truth.assertThat(DECIMAL.parseValue("0").get()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void testParseOne() {
        Truth.assertThat(DECIMAL.parseValue("1").get()).isEqualTo(BigDecimal.ONE);
    }

    @Test
    public void testParseMinusOne() {
        Truth.assertThat(DECIMAL.parseValue("-1").get()).isEqualTo(BigDecimal.ONE.negate());
    }

    @Test
    public void testParseTen() {
        Truth.assertThat(DECIMAL.parseValue("10").get()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    public void testParseLargeInteger() {
        final BigDecimal largeInteger = BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.ONE);

        Truth.assertThat(DECIMAL.parseValue(largeInteger.toString()).get()).isEqualTo(largeInteger);
    }

    @Test
    public void testParseLargeNegativeInteger() {
        final BigDecimal largeNegativeInteger = BigDecimal.valueOf(Long.MIN_VALUE).subtract(BigDecimal.ONE);

        Truth.assertThat(DECIMAL.parseValue(largeNegativeInteger.toString()).get()).isEqualTo(largeNegativeInteger);
    }

    @Test
    public void testParseZeroPrefix() {
        Truth.assertThat(DECIMAL.parseValue("010").get()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    public void testFraction() {
        Truth.assertThat(DECIMAL.parseValue("1.0").get()).isEqualTo(BigDecimal.valueOf(10, 1));
    }

    @Test
    public void testExponent() {
        Truth.assertThat(DECIMAL.parseValue("1e2").get()).isEqualTo(BigDecimal.valueOf(1, -2));
    }

    @Test
    public void testLeadingDot() {
        Truth.assertThat(DECIMAL.parseValue(".3489").get()).isEqualTo(BigDecimal.valueOf(3489, 4));
    }

    @Test
    public void testTrailingDot() {
        Truth.assertThat(DECIMAL.parseValue("1.").get()).isEqualTo(BigDecimal.ONE);
    }

    @Test
    public void testTrailingDotExponent() {
        Truth.assertThat(DECIMAL.parseValue(".3489e65").get()).isEqualTo(BigDecimal.valueOf(3489, -65 + 4));
    }

    @Test
    public void testNull() {
        Truth.assertThat(DECIMAL.parseValue(null)).isEqualTo(Optional.empty());
    }

    @Test(expected = NumberFormatException.class)
    public void testUnsupportedTwoDots() {
        DECIMAL.parseValue("1.000.000");
    }

    @Test(expected = NumberFormatException.class)
    public void testUnsupportedGroupingSeparator() {
        DECIMAL.parseValue("1,000,000.0");
    }

    @Test(expected = NumberFormatException.class)
    public void testUnsupportedHexadecimal() {
        DECIMAL.parseValue("10CEFF");
    }

}
