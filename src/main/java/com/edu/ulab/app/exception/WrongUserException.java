package com.edu.ulab.app.exception;

public class WrongUserException extends RuntimeException {
    public WrongUserException(String message) {
        super(message);
    }
}
