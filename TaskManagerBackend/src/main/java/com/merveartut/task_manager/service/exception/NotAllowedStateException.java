package com.merveartut.task_manager.service.exception;

public class NotAllowedStateException extends RuntimeException{
    public NotAllowedStateException(String message) {
        super(message);
    }
}