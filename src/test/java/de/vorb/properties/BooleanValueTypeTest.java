package de.vorb.properties;

import static de.vorb.properties.StandardValueTypes.BOOLEAN;

import java.util.Optional;

import org.junit.Test;

import com.google.common.truth.Truth;

public class BooleanValueTypeTest {

    @Test
    public void testParseTrue() {
        Truth.assertThat(BOOLEAN.parseValue("TRUE").get()).isTrue();

        Truth.assertThat(BOOLEAN.parseValue("true").get()).isTrue();
    }

    @Test
    public void testParseFalse() {
        Truth.assertThat(BOOLEAN.parseValue("FALSE").get()).isFalse();

        Truth.assertThat(BOOLEAN.parseValue("false").get()).isFalse();
    }

    @Test
    public void testParseYes() {
        Truth.assertThat(BOOLEAN.parseValue("YES").get()).isTrue();

        Truth.assertThat(BOOLEAN.parseValue("yes").get()).isTrue();
    }

    @Test
    public void testParseNo() {
        Truth.assertThat(BOOLEAN.parseValue("NO").get()).isFalse();

        Truth.assertThat(BOOLEAN.parseValue("no").get()).isFalse();
    }

    @Test
    public void testParseY() {
        Truth.assertThat(BOOLEAN.parseValue("Y").get()).isTrue();

        Truth.assertThat(BOOLEAN.parseValue("y").get()).isTrue();
    }

    @Test
    public void testParseN() {
        Truth.assertThat(BOOLEAN.parseValue("N").get()).isFalse();

        Truth.assertThat(BOOLEAN.parseValue("n").get()).isFalse();
    }

    @Test
    public void testParse1() {
        Truth.assertThat(BOOLEAN.parseValue("1").get()).isTrue();
    }

    @Test
    public void testParse0() {
        Truth.assertThat(BOOLEAN.parseValue("0").get()).isFalse();
    }

    public void testParseNull() {
        Truth.assertThat(BOOLEAN.parseValue(null)).isEqualTo(Optional.empty());
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
