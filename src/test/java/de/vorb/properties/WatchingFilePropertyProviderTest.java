package de.vorb.properties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
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
        Files.write(propertyFile,
                ImmutableList.of(
                        "test.boolean = no",
                        "test.integer = 0"),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE);

        watchingFilePropertyProvider = WatchingFilePropertyProvider.fromFile(propertyFile);

    }

    @Test
    public void testInitialProperties() {

        final Properties initialProperties = watchingFilePropertyProvider.getProperties();

        Truth.assertThat(initialProperties.getProperty("test.boolean")).isEqualTo("no");
        Truth.assertThat(initialProperties.getProperty("test.integer")).isEqualTo("0");

    }

    @Test
    public void testSubsequentPropertiesUpdate() throws IOException, InterruptedException, ExecutionException,
            TimeoutException {

        Files.write(propertyFile,
                ImmutableList.of("test.integer = 1"),
                StandardCharsets.UTF_8,
                StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

        watchingFilePropertyProvider.getPropertiesUpdate().get(30, TimeUnit.SECONDS);

        final Properties updatedProperties = watchingFilePropertyProvider.getProperties();

        Truth.assertThat(updatedProperties.getProperty("test.boolean")).isNull();

        Truth.assertThat(updatedProperties.getProperty("test.integer")).isEqualTo("1");

    }
}
