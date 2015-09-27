package de.vorb.properties.event;

import java.util.EventListener;

public interface PropertiesUpdateListener extends EventListener {
    void handlePropertiesUpdate(PropertiesUpdate updateEvent);
}
