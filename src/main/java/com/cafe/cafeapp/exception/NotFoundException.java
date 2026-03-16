package com.cafe.cafeapp.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException (String message) {
        super (message);
    }

    public NotFoundException (Long id) {
        super ("Object not found with id " + id);
    }
}
