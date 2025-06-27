package com.merveartut.task_manager.service.exception;

public class CommentNotFoundException extends RuntimeException{
    public CommentNotFoundException() {
        super("CommentNotFoundException");
    }
}
