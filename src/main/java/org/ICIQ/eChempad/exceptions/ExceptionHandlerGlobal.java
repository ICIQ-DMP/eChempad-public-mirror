package org.ICIQ.eChempad.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Used to handle the errors that occur in the application from a single class.
 */
@ControllerAdvice
public class ExceptionHandlerGlobal {

@ExceptionHandler(value = {ExceptionResourceNotExists.class})
public ResponseEntity<Object> handleResourceNotExists(ExceptionResourceNotExists except, WebRequest request)
{
    // At this point an exception of type ExceptionResourceNotExists has been produced and now we must return
    // an error response. We may do it manually by creating a ResponseEntity or we can program a new auxiliar method
    // that will be the one used to process the actual exceptions.
    return new ResponseEntity<>(except.toString(), HttpStatus.NOT_FOUND);  // new Response(body, headers, status)
    // return this.handleExceptionInternal(except, body, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
}



}
