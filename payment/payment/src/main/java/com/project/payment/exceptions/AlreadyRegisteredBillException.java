package com.project.payment.exceptions;

public class AlreadyRegisteredBillException extends RuntimeException {

    public AlreadyRegisteredBillException(String message) {
        super(message);
    }
}