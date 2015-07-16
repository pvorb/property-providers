package de.vorb.properties.parsers;

import static de.vorb.properties.parsers.ValueParsers.STRING_PARSER;

import org.junit.Test;

import com.google.common.truth.Truth;

public class StringValueParserTest {

    @Test
    public void testParseEmptyString() {
        Truth.assertThat(STRING_PARSER.parseValue("")).isEqualTo("");
    }

    @Test
    public void testParseArbitraryString() {
        Truth.assertThat(STRING_PARSER.parseValue("some arbitrary string")).isEqualTo("some arbitrary string");
    }

    @Test(expected = NullPointerException.class)
    public void testParseNull() {
        STRING_PARSER.parseValue(null);
    }

}
