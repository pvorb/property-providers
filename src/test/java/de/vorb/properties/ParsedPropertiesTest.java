package de.vorb.properties;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.google.common.truth.Truth;

public class ParsedPropertiesTest {

    private ParsedProperties parsedProperties;

    @Before
    public void setUp() throws Exception {
        final Properties source = new Properties();

        source.setProperty("boolean", "y");
        source.setProperty("integer", "0");
        source.setProperty("decimal", "0");
        source.setProperty("hexadecimal", "0x00");
        source.setProperty("string", "foo");

        parsedProperties = ParsedProperties.fromProperties(source);
    }

    @Test
    public void testGetPropertyBoolean() {
        Truth.assertThat(parsedProperties.getProperty("boolean", KeyTypes.BOOLEAN)).isTrue();
    }

    @Test
    public void testGetPropertyInteger() {
        Truth.assertThat(parsedProperties.getProperty("integer", KeyTypes.INTEGER)).isEqualTo(BigInteger.ZERO);
    }

    @Test
    public void testGetPropertyDecimal() {
        Truth.assertThat(parsedProperties.getProperty("decimal", KeyTypes.DECIMAL)).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void testGetPropertyHexadecimal() {
        Truth.assertThat(parsedProperties.getProperty("hexadecimal", KeyTypes.HEXADECIMAL))
                .isEqualTo(new byte[] { (byte) 0x00 });
    }

    @Test
    public void testGetPropertyString() {
        Truth.assertThat(parsedProperties.getProperty("string", KeyTypes.STRING)).isEqualTo("foo");
    }

}
