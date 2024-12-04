package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        return ErrorResponse.builder(e, HttpStatus.NOT_FOUND, e.getMessage()).build();
    }

    @ExceptionHandler({BookingFailedException.class, AccessDeniedException.class, IllegalDataException.class})
    public ErrorResponse handleNotFoundException(RuntimeException e) {
        return ErrorResponse.builder(e, HttpStatus.BAD_REQUEST, e.getMessage()).build();
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(final ConstraintViolationException e) {
        return e.getMessage().contains("incorrect state") ?
                new ResponseEntity<>(Collections.singletonMap("error", "incorrect state"),
                        HttpStatus.INTERNAL_SERVER_ERROR) :
                new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
