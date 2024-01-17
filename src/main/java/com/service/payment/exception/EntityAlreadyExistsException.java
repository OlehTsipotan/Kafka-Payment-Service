package com.service.payment.exception;

public class EntityAlreadyExistsException extends ServiceException{
    public EntityAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }

    public EntityAlreadyExistsException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public EntityAlreadyExistsException(Exception e) {
        super(e);
    }
}
