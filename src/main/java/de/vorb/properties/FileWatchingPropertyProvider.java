package de.vorb.properties;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import de.vorb.properties.event.PropertiesUpdate;
import de.vorb.properties.event.PropertiesUpdateListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * A {@link PropertyProvider} that listens to file system events on a given properties file and informs registered
 * {@link PropertiesUpdateListener}s of {@link PropertiesUpdate}s.
 */
public class FileWatchingPropertyProvider implements PropertyProvider, TypedProperties {

    private static final Logger logger = LoggerFactory.getLogger(FileWatchingPropertyProvider.class);

    private class PropertyFileUpdater implements Runnable {

        private final WatchService watcher;

        public PropertyFileUpdater(WatchService watcher) {
            this.watcher = watcher;
        }

        @Override
        public void run() {
            try {
                final WatchKey key = watcher.poll();

                if (key == null) {
                    return;
                }

                final List<WatchEvent<?>> pendingEvents = key.pollEvents();

                if (hasPropertyFileBeenUpdated(pendingEvents)) {
                    final Properties oldProperties = properties;
                    readPropertyFile();
                    final Properties newProperties = properties;

                    notifyUpdateListeners(oldProperties, newProperties);
                }

                key.reset();
            } catch (ClosedWatchServiceException e) {
                logger.warn("The watch service on property file '{}' has been closed.", propertyFile);
            }
        }

        @SuppressWarnings("unchecked")
        private boolean hasPropertyFileBeenUpdated(List<WatchEvent<?>> pendingWatchEvents) {
            return pendingWatchEvents.stream()
                    // We only registered to listen for ENTRY_MODIFY events, so this cast is safe
                    .map(event -> (WatchEvent<Path>) event)
                    .anyMatch(this::matchesPropertyFile);
        }

        private boolean matchesPropertyFile(WatchEvent<Path> event) {
            final Path changedFile = parentDirectory.resolve(event.context());

            try {
                return Files.isSameFile(changedFile, propertyFile);
            } catch (IOException e) {
                logger.warn("Could not access the file system", e);

                return false;
            }
        }

        private void notifyUpdateListeners(Properties oldProperties, Properties newProperties) {

            final PropertiesUpdate updateEvent = PropertiesUpdate.replacedProperties(oldProperties, newProperties);

            for (PropertiesUpdateListener listener : propertiesUpdateListeners) {
                listener.handlePropertiesUpdate(updateEvent);
            }
        }
    }

    private final Path propertyFile;
    private final Path parentDirectory;

    private final Properties defaults;
    private Properties properties;

    private final Set<PropertiesUpdateListener> propertiesUpdateListeners = Sets.newCopyOnWriteArraySet();

    private ScheduledExecutorService watchServiceExecutor;

    FileWatchingPropertyProvider(Path propertyFile, Properties defaults) {

        final boolean isRegularFile = Files.isRegularFile(propertyFile);
        final boolean isReadable = Files.isReadable(propertyFile);

        Preconditions.checkArgument(isRegularFile && isReadable,
                "Property file '%s' is no regular readable file", propertyFile);

        this.propertyFile = propertyFile;

        this.defaults = defaults;
        this.properties = new Properties(defaults);

        parentDirectory = propertyFile.getParent();

        final boolean isParentADirectory = Files.isDirectory(parentDirectory);
        final boolean isParentReadable = Files.isReadable(parentDirectory);

        Preconditions.checkArgument(isParentADirectory && isParentReadable,
                "Parent '%s' is no readable directory", parentDirectory);

        // initialize properties
        readPropertyFile();

        try {
            final WatchService watcher = createWatchService(parentDirectory);
            parentDirectory.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

            final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                    .setNameFormat(FileWatchingPropertyProvider.class.getName() + "-thread-%d")
                    .setDaemon(true)
                    .build();

            watchServiceExecutor = Executors.newSingleThreadScheduledExecutor(threadFactory);
            // watchServiceExecutor.execute(new PropertyFileUpdater(watcher));
            watchServiceExecutor.scheduleAtFixedRate(new PropertyFileUpdater(watcher), 100, 100, TimeUnit.MILLISECONDS);
        } catch (IOException e) {
            logger.error("Could not watch the parent directory of the requested property file '{}'", propertyFile, e);
        }
    }

    FileWatchingPropertyProvider(Path propertyFile) {
        this(propertyFile, new Properties());
    }

    /**
     * Adds a listener to the set of listeners.
     * 
     * @param listener
     *            will be added to the set of listeners
     */
    public void addPropertiesUpdateListener(PropertiesUpdateListener listener) {
        propertiesUpdateListeners.add(listener);
    }

    /**
     * Removes a listener from the set of listeners.
     * 
     * @param listener
     *            will be removed from the set of listeners
     */
    public void removePropertiesUpdateListener(PropertiesUpdateListener listener) {
        propertiesUpdateListeners.remove(listener);
    }

    private void readPropertyFile() {

        try (final BufferedReader propertyFileReader = Files.newBufferedReader(propertyFile)) {
            final Properties newProperties = new Properties(defaults);

            newProperties.load(propertyFileReader);

            properties = newProperties;
        } catch (IOException e) {
            logger.warn("Could not read the property file '{}'.", propertyFile);
        }

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

    protected WatchService createWatchService(Path path) throws IOException {
        return path.getFileSystem().newWatchService();
    }

    /**
     * Creates a new {@link FileWatchingPropertyProvider} from a properties file.
     * 
     * @param propertyFile
     *            a regular file that is readable and has a parent directory
     * @return new {@link FileWatchingPropertyProvider}
     */
    public static FileWatchingPropertyProvider fromFile(Path propertyFile) {
        return new FileWatchingPropertyProvider(propertyFile);
    }

    /**
     * Creates a new {@link FileWatchingPropertyProvider} from a properties file backed by the given default properties.
     * 
     * @param propertyFile
     *            a regular file that is readable and has a parent directory
     * @param defaults
     *            default properties
     * @return new {@link FileWatchingPropertyProvider}
     */
    public static FileWatchingPropertyProvider fromFileUsingDefaults(Path propertyFile, Properties defaults) {
        return new FileWatchingPropertyProvider(propertyFile, defaults);
    }

}
