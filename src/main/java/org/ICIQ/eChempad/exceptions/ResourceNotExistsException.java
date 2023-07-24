package org.ICIQ.eChempad.exceptions;

public class ResourceNotExistsException extends RuntimeException {
    public ResourceNotExistsException() {
      super();
    }

    public ResourceNotExistsException(String errorMessage) {
        super(errorMessage);
    }
}
