package de.vorb.properties;

import static de.vorb.properties.parsers.ValueParsers.BOOLEAN_PARSER;
import static de.vorb.properties.parsers.ValueParsers.DECIMAL_PARSER;
import static de.vorb.properties.parsers.ValueParsers.HEXADECIMAL_PARSER;
import static de.vorb.properties.parsers.ValueParsers.INTEGER_PARSER;
import static de.vorb.properties.parsers.ValueParsers.STRING_PARSER;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Properties;

import de.vorb.properties.exceptions.UndefinedPropertyException;
import de.vorb.properties.parsers.ValueParser;

public class ParsedProperties {
    private final Properties properties;

    private ParsedProperties(Properties properties) {
        this.properties = properties;
    }

    public boolean getBoolean(String key) {
        return BOOLEAN_PARSER.parseValue(getString(key));
    }

    public BigInteger getInteger(String key) {
        return parseValue(key, INTEGER_PARSER);
    }

    public BigDecimal getDecimal(String key) {
        return parseValue(key, DECIMAL_PARSER);
    }

    public byte[] getHexadecimal(String key) {
        return parseValue(key, HEXADECIMAL_PARSER);
    }

    public String getString(String key) {
        return parseValue(key, STRING_PARSER);
    }

    private <T> T parseValue(String key, ValueParser<T> parser) {
        try {
            return parser.parseValue(properties.getProperty(key));
        } catch (NullPointerException e) {
            throw new UndefinedPropertyException(key);
        }
    }

    public static ParsedProperties fromProperties(Properties properties) {
        return new ParsedProperties(properties);
    }

}
