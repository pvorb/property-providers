package de.vorb.properties.event;

import java.util.Properties;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.truth.Truth;

public class SuccessfulPropertiesUpdateTest {

    private final Properties oldProps = new Properties();
    private final Properties newProps = new Properties();

    @Test
    public void testGetUpdatedPropertyKeysOneChange() {
        oldProps.putAll(ImmutableMap.of(
                "test.1", "1",
                "test.2", "2"));

        newProps.putAll(ImmutableMap.of(
                "test.1", "2",
                "test.2", "2"));

        Truth.assertThat(PropertiesUpdate.replacedProperties(oldProps, newProps).getUpdatedPropertyKeys())
                .containsExactly("test.1");
    }

    @Test
    public void testGetUpdatedPropertyKeysAllNew() {
        newProps.putAll(ImmutableMap.of(
                "test.1", "1",
                "test.2", "2"));

        Truth.assertThat(PropertiesUpdate.replacedProperties(oldProps, newProps).getUpdatedPropertyKeys())
                .containsExactly("test.1", "test.2");
    }

    @Test
    public void testGetUpdatedPropertyKeysAllRemoved() {
        oldProps.putAll(ImmutableMap.of(
                "test.1", "1",
                "test.2", "2"));

        Truth.assertThat(PropertiesUpdate.replacedProperties(oldProps, newProps).getUpdatedPropertyKeys())
                .containsExactly("test.1", "test.2");
    }

    @Test
    public void testGetUpdatedPropertyKeysAllRemovedNewAdded() {
        newProps.putAll(ImmutableMap.of(
                "test.1", "1",
                "test.2", "2"));

        newProps.putAll(ImmutableMap.of(
                "test.3", "3",
                "test.4", "4"));

        Truth.assertThat(PropertiesUpdate.replacedProperties(oldProps, newProps).getUpdatedPropertyKeys())
                .containsExactly("test.1", "test.2", "test.3", "test.4");
    }

    @Test
    public void testGetUpdatedPropertyKeysNoChanges() {
        oldProps.putAll(ImmutableMap.of(
                "test.1", "1",
                "test.2", "2"));

        newProps.putAll(ImmutableMap.of(
                "test.1", "1",
                "test.2", "2"));

        Truth.assertThat(PropertiesUpdate.replacedProperties(oldProps, newProps).getUpdatedPropertyKeys()).isEmpty();
    }

}
