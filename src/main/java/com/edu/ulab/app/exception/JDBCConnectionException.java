package com.edu.ulab.app.exception;

public class JDBCConnectionException extends RuntimeException {
    public JDBCConnectionException(String message) {
        super(message);
    }
}
