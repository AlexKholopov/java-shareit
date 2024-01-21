package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.LockedException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({LockedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> bedRequestException(final Exception e) {
        log.error(e.getMessage(), e);
        return Map.of("error", e.getMessage());
    }
}
