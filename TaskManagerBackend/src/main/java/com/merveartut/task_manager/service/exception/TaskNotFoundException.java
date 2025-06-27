package com.merveartut.task_manager.service.exception;

public class TaskNotFoundException extends RuntimeException{
    public TaskNotFoundException() {
        super("TaskNotFoundException");
    }
}
