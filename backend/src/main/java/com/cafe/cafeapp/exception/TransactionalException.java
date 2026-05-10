package com.cafe.cafeapp.exception;

public class TransactionalException extends RuntimeException {
    public TransactionalException(String message) {
        super(message);
    }
}
