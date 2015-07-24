package de.vorb.properties;

import java.util.Properties;

public class ConstantPropertyProvider implements PropertyProvider {

    private final Properties properties;

    ConstantPropertyProvider(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    public static ConstantPropertyProvider fromProperties(Properties properties) {
        return new ConstantPropertyProvider(properties);
    }

}
