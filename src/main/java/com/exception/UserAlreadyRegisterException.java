package com.exception;

public class UserAlreadyRegisterException extends Exception {
    public UserAlreadyRegisterException(String message){
        super(message);
    }
}
