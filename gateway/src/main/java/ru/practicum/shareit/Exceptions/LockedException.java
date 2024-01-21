package ru.practicum.shareit.Exceptions;

public class LockedException extends RuntimeException {
    public LockedException(String message) {
        super(message);
    }
}