package com.edu.ulab.app.web.handler;

import com.edu.ulab.app.exception.*;
import com.edu.ulab.app.web.response.BaseWebResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLException;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<BaseWebResponse> handleBadRequestException(@NonNull final NotFoundException exc) {
        log.error(exc.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseWebResponse(createErrorMessage(exc)));
    }

    private String createErrorMessage(Exception exception) {
        final String message = exception.getMessage();
        log.error(ExceptionHandlerUtils.buildErrorMessage(exception));
        return message;
    }

    @ExceptionHandler({WrongUserException.class})
    public  ResponseEntity<?> handleWrongUserException(@NonNull final WrongUserException exc, WebRequest request) {
        return new ResponseEntity<>(basicActions(
                request, "User details is wrong", exc.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({WrongBookException.class})
    public  ResponseEntity<?> handleWrongBookException(@NonNull final WrongBookException exc, WebRequest request) {
        return new ResponseEntity<>(basicActions(
                request, "Book details is wrong", exc.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({JDBCConnectionException.class})
    public  ResponseEntity<?> handleWrongBookException(@NonNull final JDBCConnectionException exc, WebRequest request) {
        return new ResponseEntity<>(basicActions(
                request, "Internal database error", exc.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ApiException basicActions(WebRequest request, String message, String debugMessage) {
        log.info("Request URL : {}, Exception : {}",
                ((ServletWebRequest) request).getRequest().getRequestURI(), debugMessage);
        return new ApiException(message, debugMessage);
    }

}
