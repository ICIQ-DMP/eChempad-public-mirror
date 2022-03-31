/**
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
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
