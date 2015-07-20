package de.vorb.properties;

import static de.vorb.properties.KeyTypes.HEXADECIMAL;

import org.junit.Test;

import com.google.common.truth.Truth;

public class HexadecimalKeyTypeTest {

    private static final String HEX_CODE = "CAFE";
    private static final byte[] correspondingBytes = new byte[] { (byte) 0xCA, (byte) 0xFE };

    @Test
    public void testUnprefixed() {
        Truth.assertThat(HEXADECIMAL.parseValue(HEX_CODE)).isEqualTo(correspondingBytes);
    }

    @Test
    public void test0xPrefix() {
        final String hexCodeWith0xPrefix = "0x" + HEX_CODE;
        Truth.assertThat(HEXADECIMAL.parseValue(hexCodeWith0xPrefix)).isEqualTo(correspondingBytes);
    }

    @Test
    public void testHashPrefix() {
        final String hexCodeWithHashPrefix = "#" + HEX_CODE;
        Truth.assertThat(HEXADECIMAL.parseValue(hexCodeWithHashPrefix)).isEqualTo(correspondingBytes);
    }

    @Test(expected = NullPointerException.class)
    public void testNull() {
        HEXADECIMAL.parseValue(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmpty() {
        final String empty = "";
        HEXADECIMAL.parseValue(empty);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLeadingWhitespace() {
        final String hexCodeWithLeadingWhitespace = " " + HEX_CODE;
        HEXADECIMAL.parseValue(hexCodeWithLeadingWhitespace);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTrailingWhitespace() {
        final String hexCodeWithTrailingWhitespace = HEX_CODE + " ";
        HEXADECIMAL.parseValue(hexCodeWithTrailingWhitespace);
    }
}
