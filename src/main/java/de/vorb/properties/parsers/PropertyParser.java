package de.vorb.properties.parsers;

import java.util.Properties;

public interface PropertyParser<T> {
    T getParsedPropertyValue(String key, Properties properties);
}
