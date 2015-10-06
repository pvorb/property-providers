package de.vorb.properties;

import java.util.Optional;

/**
 * Value type that defines how string values are parsed into instances of <code>T</code>.
 *
 * @param <T>
 *            type of the value
 */
public interface ValueType<T> {

    /**
     * Parses a string value into an (optional) instance of {@code T}.
     * 
     * @param value
     *            string representation of the value
     * @return parsed value
     */
    Optional<T> parseValue(String value);

}
