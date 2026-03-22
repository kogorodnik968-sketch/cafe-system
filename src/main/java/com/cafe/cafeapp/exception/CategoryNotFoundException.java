package com.cafe.cafeapp.exception;

import lombok.Data;

@Data
public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String message) {
        super(message);
    }

    public CategoryNotFoundException(Long id) {
        super("Category not found with id" + id);
    }
}
