package com.cafe.cafeapp.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation (
            MethodArgumentNotValidException exc, HttpServletRequest request) {

        String message = exc.getBindingResult().getFieldErrors().stream().
                map(error -> error.getField() + ": " + error.getDefaultMessage()).
                findFirst().orElse("Validation error");

        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), 400, "Bad Request",
                message, request.getRequestURI());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFormat(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                400,
                "Bad Request",
                "Неверный формат данных",
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound (
            NotFoundException exc, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse( LocalDateTime.now(), 404, "Not Found",
                exc.getMessage(), request.getRequestURI());
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExists (
            AlreadyExistsException exc, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), 409, "Conflict",
                exc.getMessage(), request.getRequestURI());

        return ResponseEntity.status(409). body(error);
    }

    @ExceptionHandler(DeleteNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleDeleteNotAllowed (
            DeleteNotAllowedException exc, HttpServletRequest request) {
        return ResponseEntity.status(409).body(
                new ErrorResponse(LocalDateTime.now(), 409, "Conflict",
                        exc.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric ( Exception exc, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), 500, "Internal server Error",
                "Что-то пошло не так", request.getRequestURI());

        return ResponseEntity.status(500).body(error);
    }

}
