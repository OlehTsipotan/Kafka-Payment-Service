package com.service.payment.exception;

public class EntityNotFoundException extends ServiceException {
    public EntityNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public EntityNotFoundException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public EntityNotFoundException(Exception e) {
        super(e);
    }
}
