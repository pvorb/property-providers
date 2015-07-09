package de.vorb.properties.exceptions;

public class UndefinedPropertyException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UndefinedPropertyException(String message) {
        super(message);
    }

    public UndefinedPropertyException(String message, Throwable cause) {
        super(message, cause);
    }
}
