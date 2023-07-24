package org.ICIQ.eChempad.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS)
public class NotEnoughAuthorityException extends RuntimeException{
    public NotEnoughAuthorityException(String message) {
        super(message);
    }

    public NotEnoughAuthorityException(String message, Throwable cause) {
        super(message, cause);
    }
}
