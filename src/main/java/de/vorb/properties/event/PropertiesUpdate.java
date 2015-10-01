package de.vorb.properties.event;

import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface PropertiesUpdate {
    Optional<Throwable> getException();

    Properties getNewProperties();

    Set<String> getUpdatedPropertyKeys();

    static class SuccessfulPropertiesUpdate implements PropertiesUpdate {
        private final Set<String> changedPropertyKeys;
        private final Properties newProperties;

        public SuccessfulPropertiesUpdate(Properties oldProperties, Properties newProperties) {

            this.newProperties = newProperties;

            changedPropertyKeys =
                    Stream.concat(oldProperties.keySet().stream(), newProperties.keySet().stream())
                            .map(key -> (String) key)
                            .filter(key -> {
                                final String oldValue = oldProperties.getProperty(key);
                                final String newValue = newProperties.getProperty(key);

                                if ((oldValue == null || newValue == null) && oldValue != newValue) {
                                    return true;
                                } else {
                                    return !oldValue.equals(newValue);
                                }
                            })
                            .collect(Collectors.toSet());
        }

        @Override
        public Optional<Throwable> getException() {
            return Optional.empty();
        }

        @Override
        public Properties getNewProperties() {
            return newProperties;
        }

        @Override
        public Set<String> getUpdatedPropertyKeys() {
            return changedPropertyKeys;
        }
    }

    static PropertiesUpdate replacedProperties(Properties oldProperties, Properties newProperties) {
        return new SuccessfulPropertiesUpdate(oldProperties, newProperties);
    }
}
