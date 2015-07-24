package de.vorb.properties;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.google.common.truth.Truth;

public class ConstantPropertyProviderTest {

    private static final String SOME_OTHER_KEY = "some.other.key";

    private static final String SOME_KEY = "some.key";

    private final Properties testProperties = new Properties();
    private PropertyProvider constantPropertyProvider;

    @Before
    public void setUp() {
        testProperties.setProperty(SOME_KEY, "Some value");
        testProperties.setProperty(SOME_OTHER_KEY, "Some other value");
        constantPropertyProvider = ConstantPropertyProvider.fromProperties(testProperties);
    }

    @Test
    public void testGetProperties() {
        Truth.assertThat(constantPropertyProvider.getProperties().getProperty(SOME_KEY))
                .isEqualTo(testProperties.getProperty(SOME_KEY));
        Truth.assertThat(constantPropertyProvider.getProperties().getProperty(SOME_OTHER_KEY))
                .isEqualTo(testProperties.getProperty(SOME_OTHER_KEY));
    }

}
