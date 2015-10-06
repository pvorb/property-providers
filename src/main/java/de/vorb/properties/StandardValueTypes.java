package de.vorb.properties;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import javax.xml.bind.DatatypeConverter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

/**
 * Standard value types.
 */
public class StandardValueTypes {

    private static final ImmutableSet<String> TRUE_VALUES = ImmutableSet.of("true", "yes", "y", "1");
    private static final ImmutableSet<String> FALSE_VALUES = ImmutableSet.of("false", "no", "n", "0");

    /**
     * Boolean value type. Accepted values are <code>true</code>, <code>false</code>, <code>yes</code>, <code>no</code>,
     * <code>y</code>, <code>n</code>, <code>1</code> and <code>0</code>.
     */
    public static final ValueType<Boolean> BOOLEAN = new ValueType<Boolean>() {
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

    /**
     * Value type for all kinds of integer values.
     */
    public static final ValueType<BigInteger> INTEGER = new ValueType<BigInteger>() {
        @Override
        public Optional<BigInteger> parseValue(String value) {
            if (value == null) {
                return Optional.empty();
            } else {
                return Optional.of(new BigInteger(value));
            }
        }
    };

    /**
     * Value type for all kinds of decimal (non-integer) values.
     */
    public static final ValueType<BigDecimal> DECIMAL = new ValueType<BigDecimal>() {
        @Override
        public Optional<BigDecimal> parseValue(String value) {
            if (value == null) {
                return Optional.empty();
            } else {
                return Optional.of(new BigDecimal(value));
            }
        }
    };

    /**
     * Value type for all kinds of string values.
     */
    public static final ValueType<String> STRING = new ValueType<String>() {
        @Override
        public Optional<String> parseValue(String value) {
            return Optional.ofNullable(value);
        }
    };

    /**
     * Value type for hexadecimal values. Values may be plain hex strings or can optionally begin with either
     * <code>0x</code> or <code>#</code>.
     */
    public static final ValueType<byte[]> HEXADECIMAL = new ValueType<byte[]>() {
        @Override
        public Optional<byte[]> parseValue(String value) {
            if (value == null) {
                return Optional.empty();
            }

            Preconditions.checkArgument(!value.isEmpty(), "Empty string");
            Preconditions.checkArgument(!isStringSurroundedByWhitespace(value), "String is surrounded by whitespace");

            final String hexCodeAsString;
            if (value.startsWith("0x")) {
                hexCodeAsString = value.substring("0x".length());
            } else if (value.startsWith("#")) {
                hexCodeAsString = value.substring("#".length());
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

    private StandardValueTypes() {
    }

}
