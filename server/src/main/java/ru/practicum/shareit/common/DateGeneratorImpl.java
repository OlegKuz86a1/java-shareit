package ru.practicum.shareit.common;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateGeneratorImpl implements DateGenerator {

    @Override
    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

}
