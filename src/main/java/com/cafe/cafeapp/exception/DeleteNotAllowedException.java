package com.cafe.cafeapp.exception;

public class DeleteNotAllowedException extends RuntimeException {
    public DeleteNotAllowedException(Long id) {
        super("Есть связанные данные, нельзя удалить объект с id " + id);
    }
}
