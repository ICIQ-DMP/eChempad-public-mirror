package org.ICIQ.eChempad.exceptions;

public class ExceptionResourceNotExists extends RuntimeException {
    public ExceptionResourceNotExists() {
      super();
    }

    public ExceptionResourceNotExists(String errorMessage) {
        super(errorMessage);
    }
}
