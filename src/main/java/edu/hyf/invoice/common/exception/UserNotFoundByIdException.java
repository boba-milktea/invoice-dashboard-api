package edu.hyf.invoice.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)

public class UserNotFoundByIdException extends RuntimeException{
    public UserNotFoundByIdException(UUID id) {
        super("User not found with id: " + id);
    }
}
