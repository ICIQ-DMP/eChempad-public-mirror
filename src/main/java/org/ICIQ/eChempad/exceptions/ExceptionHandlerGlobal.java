/*
 * eChempad is a suite of web services oriented to manage the entire
 * data life-cycle of experiments and assays from Experimental
 * Chemistry and related Science disciplines.
 *
 * Copyright (C) 2021 - present Institut Català d'Investigació Química (ICIQ)
 *
 * eChempad is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
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