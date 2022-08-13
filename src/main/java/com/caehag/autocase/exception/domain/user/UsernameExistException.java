package com.caehag.autocase.exception.domain.user;

public class UsernameExistException extends Exception {
    public UsernameExistException(String message) {
        super(message);
    }
}
