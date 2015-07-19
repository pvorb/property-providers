package de.vorb.properties.exceptions;

public class UndefinedPropertyException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UndefinedPropertyException(String key) {
        super(errorMessage(key));
    }

    public UndefinedPropertyException(String key, Throwable cause) {
        super(errorMessage(key), cause);
    }

    private static String errorMessage(String key) {
        return String.format("No property found for key '%s'", key);
    }
}
