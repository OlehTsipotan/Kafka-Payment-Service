package com.service.payment.exception;

public class InsufficientReserveBalanceException extends ValidationException {
    public InsufficientReserveBalanceException(String errorMessage) {
        super(errorMessage);
    }
}
