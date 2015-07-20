package de.vorb.properties;

import java.util.Properties;

public class ParsedProperties {

    private final Properties properties;

    private ParsedProperties(Properties properties) {
        this.properties = properties;
    }

    public <T> T getProperty(String key, KeyType<T> type) {
        final String unparsedValue = properties.getProperty(key);
        return type.parseValue(unparsedValue);
    }

    public static ParsedProperties fromProperties(Properties properties) {
        return new ParsedProperties(properties);
    }

}
