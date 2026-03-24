package com.cafe.cafeapp.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(Long id) {
        super ("You can't delete object with id " + id);
    }
}
