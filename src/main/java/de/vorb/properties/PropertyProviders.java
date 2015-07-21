package de.vorb.properties;

import java.nio.file.Path;
import java.util.Properties;

public class PropertyProviders {

    public static PropertyProvider newConstantProvider(Properties properties) {
        return new ConstantPropertyProvider(properties);
    }

    public static WatchingFilePropertyProvider newWatchingProvider(Path propertyFile) {
        return new WatchingFilePropertyProvider(propertyFile);
    }

}
