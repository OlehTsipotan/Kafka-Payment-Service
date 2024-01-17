package com.service.payment.exception;

public class InsufficientAvailableBalanceException extends ValidationException {
    public InsufficientAvailableBalanceException(String errorMessage) {
        super(errorMessage);
    }
}
