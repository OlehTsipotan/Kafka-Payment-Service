package com.service.payment.exception;

public class InsufficientAvailableBalanceException extends ServiceException {
    public InsufficientAvailableBalanceException(String errorMessage) {
        super(errorMessage);
    }
}
