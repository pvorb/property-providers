package de.vorb.properties.parsers;

import static de.vorb.properties.parsers.ValueParsers.BOOLEAN_PARSER;

import org.junit.Test;

import com.google.common.truth.Truth;

public class BooleanValueParserTest {

    @Test
    public void testParseTrue() {
        Truth.assertThat(BOOLEAN_PARSER.parseValue("TRUE")).isTrue();

        Truth.assertThat(BOOLEAN_PARSER.parseValue("true")).isTrue();
    }

    @Test
    public void testParseFalse() {
        Truth.assertThat(BOOLEAN_PARSER.parseValue("FALSE")).isFalse();

        Truth.assertThat(BOOLEAN_PARSER.parseValue("false")).isFalse();
    }

    @Test
    public void testParseYes() {
        Truth.assertThat(BOOLEAN_PARSER.parseValue("YES")).isTrue();

        Truth.assertThat(BOOLEAN_PARSER.parseValue("yes")).isTrue();
    }

    @Test
    public void testParseNo() {
        Truth.assertThat(BOOLEAN_PARSER.parseValue("NO")).isFalse();

        Truth.assertThat(BOOLEAN_PARSER.parseValue("no")).isFalse();
    }

    @Test
    public void testParseY() {
        Truth.assertThat(BOOLEAN_PARSER.parseValue("Y")).isTrue();

        Truth.assertThat(BOOLEAN_PARSER.parseValue("y")).isTrue();
    }

    @Test
    public void testParseN() {
        Truth.assertThat(BOOLEAN_PARSER.parseValue("N")).isFalse();

        Truth.assertThat(BOOLEAN_PARSER.parseValue("n")).isFalse();
    }

    @Test
    public void testParse1() {
        Truth.assertThat(BOOLEAN_PARSER.parseValue("1")).isTrue();
    }

    @Test
    public void testParse0() {
        Truth.assertThat(BOOLEAN_PARSER.parseValue("0")).isFalse();
    }

    @Test(expected = NullPointerException.class)
    public void testParseNull() {
        BOOLEAN_PARSER.parseValue(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseEmptyString() {
        BOOLEAN_PARSER.parseValue("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseUnknownString() {
        BOOLEAN_PARSER.parseValue("unknown");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseLeadingWhitespace() {
        BOOLEAN_PARSER.parseValue(" true");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseTrailingWhitespace() {
        BOOLEAN_PARSER.parseValue("true ");
    }

}
