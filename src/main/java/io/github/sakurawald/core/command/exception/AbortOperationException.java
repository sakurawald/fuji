package io.github.sakurawald.core.command.exception;

public class AbortOperationException extends RuntimeException {

    public AbortOperationException(String message) {
        super(message);
    }

    public AbortOperationException() {
    }
}
