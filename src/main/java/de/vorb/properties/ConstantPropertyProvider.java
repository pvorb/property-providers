package de.vorb.properties;

import java.util.Optional;
import java.util.Properties;

/**
 * A {@link PropertyProvider} that does not change its values.
 */
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

    /**
     * Creates a {@link ConstantPropertyProvider} from a given {@link Properties} object.
     * 
     * @param properties
     *            source properties
     * @return a new {@link ConstantPropertyProvider}
     */
    public static ConstantPropertyProvider fromProperties(Properties properties) {
        return new ConstantPropertyProvider(properties);
    }

}
