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
 * We extend the class ResponseEntityExceptionHandler to obtain methods like handleExceptionInternal
 * which will be used to generate automatically (already programmed by Spring) the response bodies of
 * the HTTP response based in the received HTTP headers.
 */
@ControllerAdvice
public class ExceptionHandlerGlobal {

@ExceptionHandler(value = {ExceptionResourceNotExists.class})
public ResponseEntity<String> handleResourceNotExists(ExceptionResourceNotExists except, WebRequest request)
{
    String body = "ERROR: The resource with the requested ID does not exist.";
    return new ResponseEntity<String>(body, HttpStatus.NOT_FOUND);
    // return this.handleExceptionInternal(except, body, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
}



}
