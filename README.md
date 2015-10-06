# property-providers [![Build Status](https://travis-ci.org/pvorb/property-providers.svg?branch=master)](https://travis-ci.org/pvorb/property-providers) [![Code Coverage](http://codecov.io/github/pvorb/property-providers/coverage.svg?branch=master)](http://codecov.io/github/pvorb/property-providers?branch=master)

property-providers is a lightweight library for providing
`java.util.Properties` for your projects. At its heart there is the
`FileWatchingPropertyProvider`, which uses `java.nio.file.WatchService` to
detect changes in a property file and reloads the property file once a change is
detected. A `PropertiesUpdateListener` can be registered to be informed of
update events.

## Maven

The library is available on
[Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22de.vorb%22%20AND%20a%3A%22property-providers%22).

## JavaDoc

[JavaDoc API](http://www.javadoc.io/doc/de.vorb/property-providers)

## License

MIT License
