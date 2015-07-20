package de.vorb.properties;

public interface KeyType<T> {

    T parseValue(String value);

}
