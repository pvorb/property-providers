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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.vorb.properties.event.PropertiesUpdateListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.common.truth.Truth;

public class FileWatchingPropertyProviderTest {

    private final Logger logger = LoggerFactory.getLogger(FileWatchingPropertyProvider.class);

    private FileSystem fileSystem;

    private Path propertyFile;
    private FileWatchingPropertyProvider watchingFilePropertyProvider;

    @Before
    public void setUp() throws Exception {

        fileSystem = Jimfs.newFileSystem(Configuration.unix());

        final Path parentDirectory = fileSystem.getPath("test-directory");
        Files.createDirectories(parentDirectory);

        propertyFile = parentDirectory.resolve("test.properties");

        updatePropertyFile("1", "2");

        initPropertyProvider();

    }

    @After
    public void tearDown() throws IOException {

        Files.delete(propertyFile);
        Files.delete(propertyFile.getParent());

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
                    pollingTime.set(watchService, 50);
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
    public void testSubsequentPropertiesUpdate() throws IOException, InterruptedException, ExecutionException,
            TimeoutException {

        final CountDownLatch firstUpdateCountDownLatch = new CountDownLatch(1);

        final PropertiesUpdateListener countDownFirstLatch = listener -> {
            firstUpdateCountDownLatch.countDown();
        };

        watchingFilePropertyProvider.addPropertiesUpdateListener(countDownFirstLatch);

        updatePropertyFile(null, "3");

        Truth.assertThat(firstUpdateCountDownLatch.await(1, TimeUnit.SECONDS)).named("updateCountDownLatch").isTrue();

        watchingFilePropertyProvider.removePropertiesUpdateListener(countDownFirstLatch);

        final CountDownLatch subsequentUpdateCountDownLatch = new CountDownLatch(1);

        final PropertiesUpdateListener countDownSubsequentLatch = listener -> {
            subsequentUpdateCountDownLatch.countDown();
        };

        watchingFilePropertyProvider.addPropertiesUpdateListener(countDownSubsequentLatch);

        updatePropertyFile("1", null);

        Truth.assertThat(subsequentUpdateCountDownLatch.await(1, TimeUnit.SECONDS)).named("updateCountDownLatch")
                .isTrue();

        watchingFilePropertyProvider.removePropertiesUpdateListener(countDownSubsequentLatch);

        final Properties subsequentlyUpdatedProperties = watchingFilePropertyProvider.getProperties();
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
