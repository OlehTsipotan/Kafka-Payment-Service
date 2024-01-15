package com.service.payment.exception;

public class ServiceException extends RuntimeException {

    public ServiceException(String errorMessage) {
        super(errorMessage);
    }

    public ServiceException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public ServiceException(Exception e) {
        super(e);
    }
}
