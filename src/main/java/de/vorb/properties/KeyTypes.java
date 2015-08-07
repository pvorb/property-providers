package de.vorb.properties;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import javax.xml.bind.DatatypeConverter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

public class KeyTypes {

    private static final ImmutableSet<String> TRUE_VALUES = ImmutableSet.of("true", "yes", "y", "1");
    private static final ImmutableSet<String> FALSE_VALUES = ImmutableSet.of("false", "no", "n", "0");

    public static final KeyType<Boolean> BOOLEAN = new KeyType<Boolean>() {
        @Override
        public Optional<Boolean> parseValue(String value) {
            if (value == null) {
                return Optional.empty();
            }

            final String lowerCaseValue = value.toLowerCase();

            if (TRUE_VALUES.contains(lowerCaseValue)) {
                return Optional.of(Boolean.TRUE);
            } else if (FALSE_VALUES.contains(lowerCaseValue)) {
                return Optional.of(Boolean.FALSE);
            } else {
                throw new IllegalArgumentException(String.format("Invalid boolean property value '%s'", value));
            }
        }
    };

    public static final KeyType<BigInteger> INTEGER = new KeyType<BigInteger>() {
        @Override
        public Optional<BigInteger> parseValue(String value) {
            if (value == null) {
                return Optional.empty();
            } else {
                return Optional.of(new BigInteger(value));
            }
        }
    };

    public static final KeyType<BigDecimal> DECIMAL = new KeyType<BigDecimal>() {
        @Override
        public Optional<BigDecimal> parseValue(String value) {
            if (value == null) {
                return Optional.empty();
            } else {
                return Optional.of(new BigDecimal(value));
            }
        }
    };

    public static final KeyType<String> STRING = new KeyType<String>() {
        @Override
        public Optional<String> parseValue(String value) {
            return Optional.ofNullable(value);
        }
    };

    public static final KeyType<byte[]> HEXADECIMAL = new KeyType<byte[]>() {
        @Override
        public Optional<byte[]> parseValue(String value) {
            if (value == null) {
                return Optional.empty();
            }

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

            return Optional.of(DatatypeConverter.parseHexBinary(hexCodeAsString));
        }
    };

    private static boolean isStringSurroundedByWhitespace(String value) {
        return Character.isWhitespace(value.charAt(0))
                || Character.isWhitespace(value.charAt(value.length() - 1));
    }

    private KeyTypes() {
    }

}
