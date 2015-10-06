package de.vorb.properties.event;

import java.util.EventListener;

/**
 * Listener to properties update events.
 */
public interface PropertiesUpdateListener extends EventListener {

    /**
     * Handles an <code>updateEvent</code>.
     * 
     * @param updateEvent
     *            the properties update event
     */
    void handlePropertiesUpdate(PropertiesUpdate updateEvent);

}
