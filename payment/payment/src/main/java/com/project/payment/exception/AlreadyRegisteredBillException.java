package com.project.payment.exception;

public class AlreadyRegisteredBillException extends RuntimeException {

    public AlreadyRegisteredBillException(String message) {
        super(message);
    }
}