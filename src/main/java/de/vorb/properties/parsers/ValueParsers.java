package de.vorb.properties.parsers;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.bind.DatatypeConverter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

public class ValueParsers {
    private static final ImmutableSet<String> TRUE_VALUES = ImmutableSet.of("true", "yes", "y", "1");
    private static final ImmutableSet<String> FALSE_VALUES = ImmutableSet.of("false", "no", "n", "0");

    public static final ValueParser<Boolean> BOOLEAN_PARSER = new ValueParser<Boolean>() {
        @Override
        public Boolean parseValue(String value) {
            final String lowerCaseValue = value.toLowerCase();

            if (TRUE_VALUES.contains(lowerCaseValue)) {
                return Boolean.TRUE;
            } else if (FALSE_VALUES.contains(lowerCaseValue)) {
                return Boolean.FALSE;
            } else {
                throw new IllegalArgumentException(String.format("Invalid boolean property value '%s'", value));
            }
        }
    };

    public static final ValueParser<BigInteger> INTEGER_PARSER = new ValueParser<BigInteger>() {
        @Override
        public BigInteger parseValue(String value) {
            return new BigInteger(value);
        }
    };

    public static final ValueParser<BigDecimal> DECIMAL_PARSER = new ValueParser<BigDecimal>() {
        @Override
        public BigDecimal parseValue(String value) {
            return new BigDecimal(value);
        }
    };

    public static final ValueParser<String> STRING_PARSER = new ValueParser<String>() {
        @Override
        public String parseValue(String value) {
            Preconditions.checkNotNull(value);
            return value;
        }
    };

    public static final ValueParser<byte[]> HEXADECIMAL_PARSER = new ValueParser<byte[]>() {
        @Override
        public byte[] parseValue(String value) {
            Preconditions.checkArgument(!value.isEmpty(), "Empty string");
            Preconditions.checkArgument(!isStringSurroundedByWhitespace(value), "String is surrounded by whitespace");

            final String hexCodeAsString;
            if (value.startsWith("0x")) {
                hexCodeAsString = value.substring(2);
            } else if (value.startsWith("#")) {
                hexCodeAsString = value.substring(1);
            } else {
                hexCodeAsString = value;
            }

            return DatatypeConverter.parseHexBinary(hexCodeAsString);
        }
    };

    public static final ValueParser<byte[]> BASE64_PARSER = new ValueParser<byte[]>() {
        @Override
        public byte[] parseValue(String value) {
            Preconditions.checkArgument(!value.isEmpty(), "Empty string");
            Preconditions.checkArgument(!isStringSurroundedByWhitespace(value), "String is surrounded by whitespace");

            return DatatypeConverter.parseBase64Binary(value);
        }
    };

    private static boolean isStringSurroundedByWhitespace(String value) {
        return Character.isWhitespace(value.charAt(0))
                || Character.isWhitespace(value.charAt(value.length() - 1));
    }

    private ValueParsers() {
    }

}
