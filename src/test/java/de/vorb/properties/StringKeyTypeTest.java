package de.vorb.properties;

import static de.vorb.properties.KeyTypes.STRING;

import org.junit.Test;

import com.google.common.truth.Truth;

public class StringKeyTypeTest {

    @Test
    public void testParseEmptyString() {
        Truth.assertThat(STRING.parseValue("")).isEqualTo("");
    }

    @Test
    public void testParseArbitraryString() {
        Truth.assertThat(STRING.parseValue("some arbitrary string")).isEqualTo("some arbitrary string");
    }

    @Test(expected = NullPointerException.class)
    public void testParseNull() {
        STRING.parseValue(null);
    }

}
