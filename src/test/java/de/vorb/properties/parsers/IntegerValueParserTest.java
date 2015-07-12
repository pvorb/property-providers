package de.vorb.properties.parsers;

import static de.vorb.properties.parsers.ValueParsers.INTEGER_PARSER;

import java.math.BigInteger;

import org.junit.Test;

import com.google.common.truth.Truth;

public class IntegerValueParserTest {
    @Test
    public void testParseZero() {
        Truth.assertThat(INTEGER_PARSER.parseValue("0")).isEqualTo(BigInteger.ZERO);
    }

    @Test
    public void testParseOne() {
        Truth.assertThat(INTEGER_PARSER.parseValue("1")).isEqualTo(BigInteger.ONE);
    }

    @Test
    public void testParseMinusOne() {
        Truth.assertThat(INTEGER_PARSER.parseValue("-1")).isEqualTo(BigInteger.ONE.negate());
    }

    @Test
    public void testParseTenInDecimalFormat() {
        Truth.assertThat(INTEGER_PARSER.parseValue("10")).isEqualTo(BigInteger.TEN);
    }

    @Test
    public void testParseLargeInteger() {
        final BigInteger largeInteger = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);

        Truth.assertThat(INTEGER_PARSER.parseValue(largeInteger.toString())).isEqualTo(largeInteger);
    }

    @Test
    public void testParseLargeNegativeInteger() {
        final BigInteger largeNegativeInteger = BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE);

        Truth.assertThat(INTEGER_PARSER.parseValue(largeNegativeInteger.toString())).isEqualTo(largeNegativeInteger);
    }

    @Test
    public void testParseZeroPrefix() {
        Truth.assertThat(INTEGER_PARSER.parseValue("010")).isEqualTo(BigInteger.TEN);
    }

    @Test(expected = NumberFormatException.class)
    public void testFraction() {
        INTEGER_PARSER.parseValue("1.0");
    }

    @Test(expected = NumberFormatException.class)
    public void testExponent() {
        INTEGER_PARSER.parseValue("1e2");
    }

    @Test(expected = NumberFormatException.class)
    public void testHexadecimal() {
        INTEGER_PARSER.parseValue("10CEFF");
    }
}
