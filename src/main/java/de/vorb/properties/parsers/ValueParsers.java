package de.vorb.properties.parsers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ValueParsers {
    private static final Set<String> TRUE_VALUES = new HashSet<>(Arrays.asList("true", "yes", "y", "1"));
    private static final Set<String> FALSE_VALUES = new HashSet<>(Arrays.asList("false", "no", "n", "0"));

    public static final ValueParser<Boolean> BOOLEAN_PARSER = (String value) -> {
        final String lowerCaseValue = value.toLowerCase();

        if (TRUE_VALUES.contains(lowerCaseValue)) {
            return Boolean.TRUE;
        } else if (FALSE_VALUES.contains(lowerCaseValue)) {
            return Boolean.FALSE;
        } else {
            throw new IllegalArgumentException(String.format("Invalid boolean property value '%s'", value));
        }
    };

    private ValueParsers() {
    }

}
