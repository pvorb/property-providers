package de.vorb.properties;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.jimfs.Jimfs;
import com.google.common.truth.Truth;

public class FileWatchingPropertyProviderTest {

    private final Logger logger = LoggerFactory.getLogger(FileWatchingPropertyProvider.class);

    private FileSystem fileSystem;

    private Path propertyFile;
    private FileWatchingPropertyProvider watchingFilePropertyProvider;

    @Before
    public void setUp() throws Exception {

        fileSystem = Jimfs.newFileSystem();

        final Path parentDirectory = fileSystem.getPath("test-directory");
        Files.createDirectories(parentDirectory);

        propertyFile = parentDirectory.resolve("test.properties");

        updatePropertyFile("1", "2");

        initPropertyProvider();

    }

    private void initPropertyProvider() throws ClassNotFoundException, NoSuchFieldException,
            SecurityException {

        final Class<?> pollingWatchServiceClass = Class.forName("com.google.common.jimfs.PollingWatchService");

        final Field pollingTime = pollingWatchServiceClass.getDeclaredField("pollingTime");
        pollingTime.setAccessible(true);

        final Field timeUnit = pollingWatchServiceClass.getDeclaredField("timeUnit");
        timeUnit.setAccessible(true);

        watchingFilePropertyProvider = new FileWatchingPropertyProvider(propertyFile) {
            @Override
            protected WatchService createWatchService(Path path) throws IOException {
                final WatchService watchService = super.createWatchService(path);
                try {
                    pollingTime.set(watchService, 5);
                    timeUnit.set(watchService, TimeUnit.MILLISECONDS);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    logger.warn("Could not reduce the polling time of the watch service via reflection. "
                            + "Tests might take longer than usual.", e);
                }
                return watchService;
            }
        };

    }

    @Test
    public void testInitialProperties() {

        final Properties initialProperties = watchingFilePropertyProvider.getProperties();

        Truth.assertThat(initialProperties.getProperty("test.first")).isEqualTo("1");
        Truth.assertThat(initialProperties.getProperty("test.second")).isEqualTo("2");

    }

    @Test
    public void testPropertiesUpdate() throws IOException, InterruptedException, ExecutionException,
            TimeoutException {

        updatePropertyFile(null, "3");

        watchingFilePropertyProvider.getPropertiesUpdate().get(10, TimeUnit.SECONDS);

        final Properties updatedProperties = watchingFilePropertyProvider.getProperties();

        assertThatPropertiesMatch(updatedProperties, null, "3");

    }

    @Test
    public void testSubsequentPropertiesUpdate() throws IOException, InterruptedException, ExecutionException,
            TimeoutException {

        testPropertiesUpdate();

        final Properties updatedProperties = watchingFilePropertyProvider.getProperties();

        updatePropertyFile("1", null);

        watchingFilePropertyProvider.getPropertiesUpdate().get(10, TimeUnit.SECONDS);

        final Properties subsequentlyUpdatedProperties = watchingFilePropertyProvider.getProperties();

        assertThatPropertiesMatch(updatedProperties, null, "3");
        assertThatPropertiesMatch(subsequentlyUpdatedProperties, "1", null);

    }

    private void updatePropertyFile(String first, String second) throws IOException {
        final List<String> lines = Lists.newArrayList();

        if (first != null) {
            lines.add("test.first = " + first);
        }

        if (second != null) {
            lines.add("test.second = " + second);
        }

        Files.write(propertyFile, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.SYNC);
    }

    private void assertThatPropertiesMatch(Properties properties, String first, String second) {
        Truth.assertThat(properties.getProperty("test.first")).isEqualTo(first);
        Truth.assertThat(properties.getProperty("test.second")).isEqualTo(second);
    }

    @Test
    public void testFromFile() {
        FileWatchingPropertyProvider.fromFile(propertyFile);
    }

    @Test
    public void testFromFileUsingDefaults() {
        final Properties defaults = new Properties();
        defaults.setProperty("test.default", "default");

        final FileWatchingPropertyProvider watchingFilePropertyProvider =
                FileWatchingPropertyProvider.fromFileUsingDefaults(propertyFile, defaults);

        Truth.assertThat(watchingFilePropertyProvider.getProperties().getProperty("test.default")).isEqualTo("default");
    }

}
