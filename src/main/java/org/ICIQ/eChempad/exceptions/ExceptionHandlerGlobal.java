/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Used to handle the errors that are thrown in the last level of the application (controller level). Each method is
 * executed for a certain Exception.
 * That exception is mapped to a certain response, and for each excpetion we can customize the Error response by using
 * different implementations of the response and various constructors.
 *      * manipulate the HTTP status, the headers, body, etc.
 */
@ControllerAdvice
public class ExceptionHandlerGlobal {

    /**
     * Maps a ExceptionResourceNotExists ultimately thrown from our controllers into a custom HTTP Response
     * @param except Data of the exception that has been thrown
     * @param request Request that made our exception be thrown
     * @return HTTP Response.
     */
    @ExceptionHandler(value = {ResourceNotExistsException.class})
    public ResponseEntity<Object> handleResourceNotExists(ResourceNotExistsException except, WebRequest request)
    {
        return new ResponseEntity<>(except.toString(), HttpStatus.NOT_FOUND);
    }



}