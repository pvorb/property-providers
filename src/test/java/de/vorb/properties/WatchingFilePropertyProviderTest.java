package de.vorb.properties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.jimfs.Jimfs;
import com.google.common.truth.Truth;

public class WatchingFilePropertyProviderTest {

    private Path propertyFile;
    private WatchingFilePropertyProvider watchingFilePropertyProvider;

    @Before
    public void setUp() throws IOException {

        final FileSystem fs = Jimfs.newFileSystem();

        final Path parentDirectory = fs.getPath("test-directory");
        Files.createDirectories(parentDirectory);

        propertyFile = parentDirectory.resolve("test.properties");

        updatePropertyFile("1", "2");

        watchingFilePropertyProvider = WatchingFilePropertyProvider.fromFile(propertyFile);

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

        watchingFilePropertyProvider.getPropertiesUpdate().get(30, TimeUnit.SECONDS);

        final Properties updatedProperties = watchingFilePropertyProvider.getProperties();

        Truth.assertThat(updatedProperties.getProperty("test.first")).isNull();
        Truth.assertThat(updatedProperties.getProperty("test.second")).isEqualTo("3");

    }

    @Test
    public void testSubsequentPropertiesUpdate() throws IOException, InterruptedException, ExecutionException,
            TimeoutException {

        updatePropertyFile("2", "1");

        watchingFilePropertyProvider.getPropertiesUpdate().get(30, TimeUnit.SECONDS);

        final Properties updatedProperties1 = watchingFilePropertyProvider.getProperties();

        updatePropertyFile("3", "4");

        watchingFilePropertyProvider.getPropertiesUpdate().get(30, TimeUnit.SECONDS);

        final Properties updatedProperties2 = watchingFilePropertyProvider.getProperties();

        // assert that a retrieved Properties object is not updated after a change
        Truth.assertThat(updatedProperties1.getProperty("test.first")).isEqualTo("2");
        Truth.assertThat(updatedProperties1.getProperty("test.second")).isEqualTo("1");

        // assert that the second change reflects the new values
        Truth.assertThat(updatedProperties2.getProperty("test.first")).isEqualTo("3");
        Truth.assertThat(updatedProperties2.getProperty("test.second")).isEqualTo("4");

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
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

}
