package ru.practicum.shareit.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {

    String error;

    public ErrorResponse(String error) {
        this.error = error;
    }
}