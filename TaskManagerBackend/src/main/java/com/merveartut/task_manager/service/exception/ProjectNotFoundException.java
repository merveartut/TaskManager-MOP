package com.merveartut.task_manager.service.exception;

public class ProjectNotFoundException extends RuntimeException{
    public ProjectNotFoundException() {
        super("ProjectNotFoundException");
    }
}
