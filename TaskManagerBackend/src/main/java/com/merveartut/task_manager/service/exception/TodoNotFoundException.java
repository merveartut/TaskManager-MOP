package com.merveartut.task_manager.service.exception;

public class TodoNotFoundException extends RuntimeException {
    public TodoNotFoundException() {
        super("Todo not found");
    }
}
