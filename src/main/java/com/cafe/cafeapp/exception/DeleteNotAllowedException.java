package com.cafe.cafeapp.exception;

public class DeleteNotAllowedException extends RuntimeException {
    public DeleteNotAllowedException(Long id) {
        super("Нельзя удалить, есть связанные данные");
    }
}
