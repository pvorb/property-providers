package de.vorb.properties.parsers;

public abstract class AbstractPropertyParser<T> implements PropertyParser<T> {

    private final ValueParser<T> valueParser;

    protected AbstractPropertyParser(ValueParser<T> valueParser) {
        this.valueParser = valueParser;
    }

    protected ValueParser<T> getValueParser() {
        return valueParser;
    }

}
