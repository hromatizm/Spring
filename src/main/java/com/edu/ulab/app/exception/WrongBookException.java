package com.edu.ulab.app.exception;

public class WrongBookException extends RuntimeException {
    public WrongBookException(String message) {
        super(message);
    }
}
