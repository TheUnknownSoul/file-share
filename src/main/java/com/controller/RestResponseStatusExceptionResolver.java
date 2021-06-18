package com.controller;

import com.exception.UserAlreadyRegisterException;
import com.exception.UserNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;



@ControllerAdvice
public class RestResponseStatusExceptionResolver extends ResponseEntityExceptionHandler {

    @ExceptionHandler({UserAlreadyRegisterException.class})
    protected ResponseEntity<Object> userExistsResolver(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "This user already exists or invalid data has been type";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({UserNotFoundException.class})
    protected ResponseEntity<Object> userNotFoundResolver(RuntimeException ex, WebRequest request) {

        String bodyOfResponse = "User not found";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({NullPointerException.class})
    protected ResponseEntity<Object> fileNotFoundResolver(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "Files not found";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
