package de.vorb.properties.parsers;

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

    private ValueParsers() {
    }

}
