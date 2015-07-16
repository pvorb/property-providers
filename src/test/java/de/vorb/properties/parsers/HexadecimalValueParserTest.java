package de.vorb.properties.parsers;

import static de.vorb.properties.parsers.ValueParsers.HEXADECIMAL_PARSER;

import org.junit.Test;

import com.google.common.truth.Truth;

public class HexadecimalValueParserTest {

    private static final String HEX_CODE = "CAFE";
    private static final byte[] correspondingBytes = new byte[] { (byte) 0xCA, (byte) 0xFE };

    @Test
    public void testUnprefixed() {
        Truth.assertThat(HEXADECIMAL_PARSER.parseValue(HEX_CODE)).isEqualTo(correspondingBytes);
    }

    @Test
    public void test0xPrefix() {
        final String hexCodeWith0xPrefix = "0x" + HEX_CODE;
        Truth.assertThat(HEXADECIMAL_PARSER.parseValue(hexCodeWith0xPrefix)).isEqualTo(correspondingBytes);
    }

    @Test
    public void testHashPrefix() {
        final String hexCodeWithHashPrefix = "#" + HEX_CODE;
        Truth.assertThat(HEXADECIMAL_PARSER.parseValue(hexCodeWithHashPrefix)).isEqualTo(correspondingBytes);
    }

    @Test(expected = NullPointerException.class)
    public void testNull() {
        HEXADECIMAL_PARSER.parseValue(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmpty() {
        final String empty = "";
        HEXADECIMAL_PARSER.parseValue(empty);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLeadingWhitespace() {
        final String hexCodeWithLeadingWhitespace = " " + HEX_CODE;
        HEXADECIMAL_PARSER.parseValue(hexCodeWithLeadingWhitespace);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTrailingWhitespace() {
        final String hexCodeWithTrailingWhitespace = HEX_CODE + " ";
        HEXADECIMAL_PARSER.parseValue(hexCodeWithTrailingWhitespace);
    }
}
