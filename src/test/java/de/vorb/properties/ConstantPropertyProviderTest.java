package de.vorb.properties;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.google.common.truth.Truth;

public class ConstantPropertyProviderTest {

    private static final String UNTYPED_KEY = "key.untyped";
    private static final String TYPED_KEY = "key.typed";

    private final Properties testProperties = new Properties();
    private ConstantPropertyProvider constantPropertyProvider;

    @Before
    public void setUp() {
        testProperties.setProperty(UNTYPED_KEY, "Arbitrary value");
        testProperties.setProperty(Boolean.class.getSimpleName(), "true");

        constantPropertyProvider = ConstantPropertyProvider.fromProperties(testProperties);
    }

    @Test
    public void testGetProperties() {
        Truth.assertThat(constantPropertyProvider.getProperties().getProperty(UNTYPED_KEY))
                .isEqualTo(testProperties.getProperty(UNTYPED_KEY));
    }

    @Test
    public void testGetTypedProperty() {
        Truth.assertThat(constantPropertyProvider.getProperty(TYPED_KEY, KeyTypes.BOOLEAN))
                .isEqualTo(KeyTypes.BOOLEAN.parseValue(testProperties.getProperty(TYPED_KEY)));
    }
}
