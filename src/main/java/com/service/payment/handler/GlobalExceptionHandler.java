package com.service.payment.handler;

import com.service.payment.exception.EntityAlreadyExistsException;
import com.service.payment.exception.EntityNotFoundException;
import com.service.payment.exception.EntityValidationException;
import com.service.payment.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorResponse handleRuntimeException(RuntimeException e) {
        return ErrorResponse.builder(e, HttpStatus.BAD_GATEWAY, e.getMessage()).title("Runtime Exception")
                .property("timestamp", Instant.now()).build();
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleServiceException(ServiceException e) {
        return ErrorResponse.builder(e, HttpStatus.SERVICE_UNAVAILABLE, e.getMessage()).title("Service Exception")
                .property("timestamp", Instant.now()).build();
    }

    @ExceptionHandler(EntityValidationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleValidationException(EntityValidationException e) {
        return ErrorResponse.builder(e, HttpStatus.CONFLICT, e.getMessage()).title("EntityValidation Exception")
                .property("violations", e.getViolations()).property("timestamp", Instant.now()).build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        return ErrorResponse.builder(e, HttpStatus.NOT_FOUND, e.getMessage()).title("EntityNotFound Exception")
                .property("timestamp", Instant.now()).build();
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEntityAlreadyExistsException(EntityAlreadyExistsException e) {
        return ErrorResponse.builder(e, HttpStatus.CONFLICT, e.getMessage()).title("EntityAlreadyExists Exception")
                .property("timestamp", Instant.now()).build();
    }
}
