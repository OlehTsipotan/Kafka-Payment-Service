package com.service.payment.exception;

public class InsufficientReserveBalanceException extends ServiceException {
    public InsufficientReserveBalanceException(String errorMessage) {
        super(errorMessage);
    }
}
