package de.vorb.properties;

import java.util.Optional;
import java.util.Properties;

public class ConstantPropertyProvider implements PropertyProvider, TypedProperties {

    private final Properties properties;

    ConstantPropertyProvider(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public <T> Optional<T> getProperty(String key, ValueType<T> type) {
        return type.parseValue(getUntypedValue(key));
    }

    @Override
    public <T> T getPropertyOrDefaultValue(String key, T defaultValue, ValueType<T> type) {
        return getProperty(key, type).orElse(defaultValue);
    }

    private String getUntypedValue(String key) {
        return getProperties().getProperty(key);
    }

    public static ConstantPropertyProvider fromProperties(Properties properties) {
        return new ConstantPropertyProvider(properties);
    }

}
