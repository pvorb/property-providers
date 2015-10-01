package de.vorb.properties;

import static de.vorb.properties.KeyTypes.INTEGER;

import java.math.BigInteger;
import java.util.Optional;

import org.junit.Test;

import com.google.common.truth.Truth;

public class IntegerKeyTypeTest {

    @Test
    public void testParseZero() {
        Truth.assertThat(INTEGER.parseValue("0").get()).isEqualTo(BigInteger.ZERO);
    }

    @Test
    public void testParseOne() {
        Truth.assertThat(INTEGER.parseValue("1").get()).isEqualTo(BigInteger.ONE);
    }

    @Test
    public void testParseMinusOne() {
        Truth.assertThat(INTEGER.parseValue("-1").get()).isEqualTo(BigInteger.ONE.negate());
    }

    @Test
    public void testParseTen() {
        Truth.assertThat(INTEGER.parseValue("10").get()).isEqualTo(BigInteger.TEN);
    }

    @Test
    public void testParseLargeInteger() {
        final BigInteger largeInteger = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);

        Truth.assertThat(INTEGER.parseValue(largeInteger.toString()).get()).isEqualTo(largeInteger);
    }

    @Test
    public void testParseLargeNegativeInteger() {
        final BigInteger largeNegativeInteger = BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE);

        Truth.assertThat(INTEGER.parseValue(largeNegativeInteger.toString()).get()).isEqualTo(largeNegativeInteger);
    }

    @Test
    public void testParseZeroPrefix() {
        Truth.assertThat(INTEGER.parseValue("010").get()).isEqualTo(BigInteger.TEN);
    }

    public void testNull() {
        Truth.assertThat(INTEGER.parseValue(null)).isEqualTo(Optional.empty());
    }

    @Test(expected = NumberFormatException.class)
    public void testUnsupportedFraction() {
        INTEGER.parseValue("1.0");
    }

    @Test(expected = NumberFormatException.class)
    public void testUnsupportedExponent() {
        INTEGER.parseValue("1e2");
    }

    @Test(expected = NumberFormatException.class)
    public void testUnsupportedHexadecimal() {
        INTEGER.parseValue("10CEFF");
    }

}
