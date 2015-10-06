package de.vorb.properties;

import java.util.Optional;

/**
 * Gives typed access to properties.
 */
public interface TypedProperties {

    /**
     * @param key
     *            key of the requested property
     * @param type
     *            type of the requested property
     * @return optional instance of the property type
     */
    <T> Optional<T> getProperty(String key, ValueType<T> type);

    /**
     * @param key
     *            key of the requested property
     * @param defaultValue
     *            default value
     * @param type
     *            type of the requested property
     * @return the parsed property value or the provided default value
     */
    <T> T getPropertyOrDefaultValue(String key, T defaultValue, ValueType<T> type);

}
