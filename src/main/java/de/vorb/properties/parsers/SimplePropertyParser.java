package de.vorb.properties.parsers;

import java.util.Properties;

import de.vorb.properties.exceptions.UndefinedPropertyException;

public class SimplePropertyParser<T> extends AbstractPropertyParser<T> {

    protected SimplePropertyParser(ValueParser<T> valueParser) {
        super(valueParser);
    }

    @Override
    public T getParsedPropertyValue(String key, Properties properties) {

        final String propertyValue = properties.getProperty(key);

        try {
            return getValueParser().parseValue(propertyValue);
        } catch (IllegalArgumentException exception) {
            throw new UndefinedPropertyException(String.format("Invalid property value for key '%s'", key), exception);
        }

    }

}
