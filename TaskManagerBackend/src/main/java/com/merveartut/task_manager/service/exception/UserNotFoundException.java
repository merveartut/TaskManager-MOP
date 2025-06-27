package com.merveartut.task_manager.service.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(){
        super("UserNotFoundException");
    }
}
