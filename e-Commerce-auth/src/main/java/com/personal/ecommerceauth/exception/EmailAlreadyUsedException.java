package com.personal.ecommerceauth.exception;

public class EmailAlreadyUsedException extends IllegalArgumentException {
    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}
