package de.vorb.properties;

import java.util.Properties;

/**
 * Reference to a set of properties that may change over time.
 */
public interface PropertyProvider {

    /**
     * @return current set of properties
     */
    Properties getProperties();

}
