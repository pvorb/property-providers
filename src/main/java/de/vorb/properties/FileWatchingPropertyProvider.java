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
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

public class FileWatchingPropertyProvider implements PropertyProvider {

    private static final Logger logger = LoggerFactory.getLogger(FileWatchingPropertyProvider.class);

    private class PropertyFileUpdater implements Runnable {

        private final WatchService watcher;

        public PropertyFileUpdater(WatchService watcher) {
            this.watcher = watcher;
        }

        @Override
        public void run() {
            watchForFileChanges();
        }

        private void watchForFileChanges() {
            try {

                while (true) {
                    final WatchKey key = watcher.take();

                    final List<WatchEvent<?>> pendingEvents = key.pollEvents();

                    if (hasPropertyFileBeenUpdated(pendingEvents)) {
                        readPropertyFile();

                        notifyUpdateListeners();
                    }

                    final boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                logger.warn(
                        "The property file '{}' is no longer being watched due to an interruption of the WatchService.",
                        propertyFile, e);
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
    }

    private final Path propertyFile;
    private final Path parentDirectory;

    private final Properties defaults;
    private Properties properties;

    private SettableFuture<Void> propertiesUpdate;

    private ExecutorService watchServiceExecutor;

    FileWatchingPropertyProvider(Path propertyFile, Properties defaults) {

        final boolean isRegularFile = Files.isRegularFile(propertyFile);
        final boolean isReadable = Files.isReadable(propertyFile);

        Preconditions.checkArgument(isRegularFile && isReadable,
                "Property file '%s' is no regular readable file", propertyFile);

        this.propertyFile = propertyFile;

        this.defaults = defaults;
        this.properties = new Properties(defaults);

        parentDirectory = propertyFile.getParent();
        propertiesUpdate = SettableFuture.create();

        final boolean isParentADirectory = Files.isDirectory(parentDirectory);
        final boolean isParentReadable = Files.isReadable(parentDirectory);

        Preconditions.checkArgument(isParentADirectory && isParentReadable,
                "Parent '%s' is no readable directory", parentDirectory);

        // initialize properties
        readPropertyFile();

        try {
            final WatchService watcher = createWatchService(parentDirectory);
            parentDirectory.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

            watchServiceExecutor = Executors.newSingleThreadExecutor();
            watchServiceExecutor.execute(new PropertyFileUpdater(watcher));
        } catch (IOException e) {
            logger.error("Could not watch the parent directory of the requested property file '{}'", propertyFile, e);
        }
    }

    FileWatchingPropertyProvider(Path propertyFile) {
        this(propertyFile, new Properties());
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

    public ListenableFuture<Void> getPropertiesUpdate() {
        return propertiesUpdate;
    }

    private void notifyUpdateListeners() {
        propertiesUpdate.set(null);
        propertiesUpdate = SettableFuture.create();
    }

    protected WatchService createWatchService(Path path) throws IOException {
        return path.getFileSystem().newWatchService();
    }

    public static FileWatchingPropertyProvider fromFile(Path propertyFile) {
        return new FileWatchingPropertyProvider(propertyFile);
    }

    public static FileWatchingPropertyProvider fromFileUsingDefaults(Path propertyFile, Properties defaults) {
        return new FileWatchingPropertyProvider(propertyFile, defaults);
    }

}
