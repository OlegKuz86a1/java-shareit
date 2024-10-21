package ru.practicum.shareit.common.exception;

import org.springframework.web.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        return org.springframework.web.ErrorResponse.builder(e, HttpStatus.NOT_FOUND, e.getMessage()).build();
    }

    @ExceptionHandler({BookingFailedException.class, AccessDeniedException.class, IllegalDataException.class})
    public ErrorResponse handleNotFoundException(RuntimeException e) {
        return org.springframework.web.ErrorResponse.builder(e, HttpStatus.BAD_REQUEST, e.getMessage()).build();
    }
}
