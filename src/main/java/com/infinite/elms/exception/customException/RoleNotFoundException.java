package com.infinite.elms.exception.customException;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}
