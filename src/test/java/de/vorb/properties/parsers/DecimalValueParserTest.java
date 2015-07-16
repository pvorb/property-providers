package de.vorb.properties.parsers;

import static de.vorb.properties.parsers.ValueParsers.DECIMAL_PARSER;

import java.math.BigDecimal;

import org.junit.Test;

import com.google.common.truth.Truth;

public class DecimalValueParserTest {
    @Test
    public void testParseZero() {
        Truth.assertThat(DECIMAL_PARSER.parseValue("0")).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void testParseOne() {
        Truth.assertThat(DECIMAL_PARSER.parseValue("1")).isEqualTo(BigDecimal.ONE);
    }

    @Test
    public void testParseMinusOne() {
        Truth.assertThat(DECIMAL_PARSER.parseValue("-1")).isEqualTo(BigDecimal.ONE.negate());
    }

    @Test
    public void testParseTen() {
        Truth.assertThat(DECIMAL_PARSER.parseValue("10")).isEqualTo(BigDecimal.TEN);
    }

    @Test
    public void testParseLargeInteger() {
        final BigDecimal largeInteger = BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.ONE);

        Truth.assertThat(DECIMAL_PARSER.parseValue(largeInteger.toString())).isEqualTo(largeInteger);
    }

    @Test
    public void testParseLargeNegativeInteger() {
        final BigDecimal largeNegativeInteger = BigDecimal.valueOf(Long.MIN_VALUE).subtract(BigDecimal.ONE);

        Truth.assertThat(DECIMAL_PARSER.parseValue(largeNegativeInteger.toString())).isEqualTo(largeNegativeInteger);
    }

    @Test
    public void testParseZeroPrefix() {
        Truth.assertThat(DECIMAL_PARSER.parseValue("010")).isEqualTo(BigDecimal.TEN);
    }

    @Test
    public void testFraction() {
        Truth.assertThat(DECIMAL_PARSER.parseValue("1.0")).isEqualTo(BigDecimal.valueOf(10, 1));
    }

    @Test
    public void testExponent() {
        Truth.assertThat(DECIMAL_PARSER.parseValue("1e2")).isEqualTo(BigDecimal.valueOf(1, -2));
    }

    @Test
    public void testLeadingDot() {
        Truth.assertThat(DECIMAL_PARSER.parseValue(".3489")).isEqualTo(BigDecimal.valueOf(3489, 4));
    }

    @Test
    public void testTrailingDot() {
        Truth.assertThat(DECIMAL_PARSER.parseValue("1.")).isEqualTo(BigDecimal.ONE);
    }

    @Test
    public void testTrailingDotExponent() {
        Truth.assertThat(DECIMAL_PARSER.parseValue(".3489e65")).isEqualTo(BigDecimal.valueOf(3489, -65 + 4));
    }

    @Test(expected = NumberFormatException.class)
    public void testUnsupportedTwoDots() {
        DECIMAL_PARSER.parseValue("1.000.000");
    }

    @Test(expected = NumberFormatException.class)
    public void testUnsupportedGroupingSeparator() {
        DECIMAL_PARSER.parseValue("1,000,000.0");
    }

    @Test(expected = NumberFormatException.class)
    public void testUnsupportedHexadecimal() {
        DECIMAL_PARSER.parseValue("10CEFF");
    }
}
