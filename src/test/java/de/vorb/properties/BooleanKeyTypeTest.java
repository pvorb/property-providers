package de.vorb.properties;

import static de.vorb.properties.KeyTypes.BOOLEAN;

import org.junit.Test;

import com.google.common.truth.Truth;

public class BooleanKeyTypeTest {

    @Test
    public void testParseTrue() {
        Truth.assertThat(BOOLEAN.parseValue("TRUE")).isTrue();

        Truth.assertThat(BOOLEAN.parseValue("true")).isTrue();
    }

    @Test
    public void testParseFalse() {
        Truth.assertThat(BOOLEAN.parseValue("FALSE")).isFalse();

        Truth.assertThat(BOOLEAN.parseValue("false")).isFalse();
    }

    @Test
    public void testParseYes() {
        Truth.assertThat(BOOLEAN.parseValue("YES")).isTrue();

        Truth.assertThat(BOOLEAN.parseValue("yes")).isTrue();
    }

    @Test
    public void testParseNo() {
        Truth.assertThat(BOOLEAN.parseValue("NO")).isFalse();

        Truth.assertThat(BOOLEAN.parseValue("no")).isFalse();
    }

    @Test
    public void testParseY() {
        Truth.assertThat(BOOLEAN.parseValue("Y")).isTrue();

        Truth.assertThat(BOOLEAN.parseValue("y")).isTrue();
    }

    @Test
    public void testParseN() {
        Truth.assertThat(BOOLEAN.parseValue("N")).isFalse();

        Truth.assertThat(BOOLEAN.parseValue("n")).isFalse();
    }

    @Test
    public void testParse1() {
        Truth.assertThat(BOOLEAN.parseValue("1")).isTrue();
    }

    @Test
    public void testParse0() {
        Truth.assertThat(BOOLEAN.parseValue("0")).isFalse();
    }

    @Test(expected = NullPointerException.class)
    public void testParseNull() {
        BOOLEAN.parseValue(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseEmptyString() {
        BOOLEAN.parseValue("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseUnknownString() {
        BOOLEAN.parseValue("unknown");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseLeadingWhitespace() {
        BOOLEAN.parseValue(" true");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseTrailingWhitespace() {
        BOOLEAN.parseValue("true ");
    }

}
