package de.vorb.properties;

import static de.vorb.properties.StandardValueTypes.STRING;

import java.util.Optional;

import org.junit.Test;

import com.google.common.truth.Truth;

public class StringValueTypeTest {

    @Test
    public void testParseEmptyString() {
        Truth.assertThat(STRING.parseValue("").get()).isEqualTo("");
    }

    @Test
    public void testParseArbitraryString() {
        Truth.assertThat(STRING.parseValue("some arbitrary string").get()).isEqualTo("some arbitrary string");
    }

    public void testParseNull() {
        Truth.assertThat(STRING.parseValue(null)).isEqualTo(Optional.empty());
    }
}
