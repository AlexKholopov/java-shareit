package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.LockedException;
import ru.practicum.shareit.exceptions.NoAuthorizationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> conflictException(final Exception e) {
        log.error(e.getMessage(), e);
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFoundException(final Exception e) {
        log.error(e.getMessage(), e);
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(NoAuthorizationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> noAuthorizationException(final Exception e) {
        log.error(e.getMessage(), e);
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler({LockedException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> bedRequestException(final Exception e) {
        log.error(e.getMessage(), e);
        return Map.of("error", e.getMessage());
    }
}

