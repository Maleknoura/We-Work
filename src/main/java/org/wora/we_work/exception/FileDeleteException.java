package org.wora.we_work.exception;

public class FileDeleteException extends RuntimeException {
    public FileDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}