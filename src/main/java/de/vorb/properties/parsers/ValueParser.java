package de.vorb.properties.parsers;

public interface ValueParser<T> {
    T parseValue(String value);
}
