package de.vorb.properties;

import java.util.Optional;

public interface TypedProperties {

    <T> Optional<T> getProperty(String key, KeyType<T> type);

    <T> T getPropertyOrDefaultValue(String key, T defaultValue, KeyType<T> type);

}
