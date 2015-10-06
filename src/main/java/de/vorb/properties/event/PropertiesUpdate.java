package de.vorb.properties.event;

import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Update event that is triggered, when a change of properties has been detected.
 */
public interface PropertiesUpdate {

    /**
     * @return optional exception that occurred while the update was detected
     */
    Optional<Throwable> getException();

    /**
     * @return the new properties
     */
    Properties getNewProperties();

    /**
     * @return the set of property keys that changed during this update
     */
    Set<String> getUpdatedPropertyKeys();

    static class SuccessfulPropertiesUpdate implements PropertiesUpdate {
        private final Set<String> updatedPropertyKeys;
        private final Properties newProperties;

        public SuccessfulPropertiesUpdate(Properties oldProperties, Properties newProperties) {

            this.newProperties = newProperties;

            updatedPropertyKeys =
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
            return updatedPropertyKeys;
        }
    }

    static PropertiesUpdate replacedProperties(Properties oldProperties, Properties newProperties) {
        return new SuccessfulPropertiesUpdate(oldProperties, newProperties);
    }
}
