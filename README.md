# property-providers [![Build Status](https://travis-ci.org/pvorb/property-providers.svg?branch=master)](https://travis-ci.org/pvorb/property-providers) [![Code Coverage](http://codecov.io/github/pvorb/property-providers/coverage.svg?branch=master)](http://codecov.io/github/pvorb/property-providers?branch=master)

property-providers is a lightweight library for providing
`java.util.Properties`for your projects. At it's heart there is the
`WatchingFilePropertyProvider`, which uses `java.nio.file.WatchService` to
detect changes in a property file and reloads the property file once a change is
detected.


## Usage

Here's a (more or less complete) example:

~~~ java
package de.vorb.properties.examples;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import de.vorb.properties.WatchingFilePropertyProvider;

public class WatchingFilePropertyProviderExample {

    private static final String SOME_PROPERTY_KEY = "some.property.key";

    @Resource
    private ExecutorService executor;

    public static void main(String[] args) {

        Path propertyFile = Paths.get("/path/to/config.properties");

        WatchingFilePropertyProvider propertyProvider = WatchingFilePropertyProvider.fromFile(propertyFile);

        // the *.properties file is loaded once on creation and can be used immediately
        Properties initialProperties = propertyProvider.getProperties();

        String propertyValue = initialProperties.getProperty(SOME_PROPERTY_KEY);

        // after a while the *.properties file have changed
        // you need to get the updated properties from the provider
        Properties updatedProperties = propertyProvider.getProperties();

        propertyValue = updatedProperties.getProperty(SOME_PROPERTY_KEY);

        // you can still use the reference to old properties
        propertyValue = initialProperties.getProperty(SOME_PROPERTY_KEY);

        // if you want to get notified when the property file is updated, you can use
        ListenableFuture<Void> futureUpdate = propertyProvider.getPropertiesUpdate();

        // listen for the next update of the properties file
        Futures.addCallback(futureUpdate, new FutureCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // the future is completed once the updated file has been loaded, so you can immediately get the new
                // properties
                Properties updatedProperties = propertyProvider.getProperties();
            }

            @Override
            public void onFailure(Throwable t) {
                // handle failure
            }
        });

    }
}
~~~
