package com.project.payment.exception;

public class BillNotFoundException extends RuntimeException {

    public BillNotFoundException(String message) {
        super(message);
    }
}