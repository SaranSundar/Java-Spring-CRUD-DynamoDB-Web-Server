package com.sszg.atlassianproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidTableException extends RuntimeException {
    public InvalidTableException(String message) {
        super(message);
    }
}
