/**
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.exceptions;

public class ResourceNotExistsException extends RuntimeException {
    public ResourceNotExistsException() {
      super();
    }

    public ResourceNotExistsException(String errorMessage) {
        super(errorMessage);
    }
}
