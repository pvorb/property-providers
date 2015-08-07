package de.vorb.properties;

import java.util.Optional;

public interface KeyType<T> {

    Optional<T> parseValue(String value);

}
