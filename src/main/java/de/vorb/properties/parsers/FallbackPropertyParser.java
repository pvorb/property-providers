package de.vorb.properties.parsers;

import java.util.Properties;

import de.vorb.properties.exceptions.UndefinedPropertyException;

public class FallbackPropertyParser<T> extends AbstractPropertyParser<T> {

    private final Properties fallbackProperties;

    public FallbackPropertyParser(Properties fallbackProperties, ValueParser<T> valueParser) {
        super(valueParser);

        this.fallbackProperties = fallbackProperties;
    }

    @Override
    public T getParsedPropertyValue(String key, Properties properties) {
        final String propertyValue = properties.getProperty(key);

        try {
            if (propertyValue != null) {
                return getValueParser().parseValue(propertyValue);
            } else {
                return getParsedPropertyValueFromFallbackProperties(key);
            }
        } catch (IllegalArgumentException exception) {
            throw new UndefinedPropertyException(String.format("Invalid property value for key '%s'", key), exception);
        }
    }

    private T getParsedPropertyValueFromFallbackProperties(String key) {
        final String fallbackValue = fallbackProperties.getProperty(key);

        if (fallbackValue != null) {
            return getValueParser().parseValue(fallbackValue);
        } else {
            throw new UndefinedPropertyException(String.format("No definition found for key '%s'", key));
        }
    }
}
