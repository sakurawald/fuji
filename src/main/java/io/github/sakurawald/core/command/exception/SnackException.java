package io.github.sakurawald.core.command.exception;

/** This exception is only used to break control flow. */
public class SnackException extends RuntimeException {

    public SnackException(String message) {
        super(message);
    }

    public SnackException() {
    }
}
