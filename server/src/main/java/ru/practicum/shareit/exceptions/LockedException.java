package ru.practicum.shareit.exceptions;

public class LockedException extends RuntimeException {
    public LockedException(String message) {
        super(message);
    }
}
