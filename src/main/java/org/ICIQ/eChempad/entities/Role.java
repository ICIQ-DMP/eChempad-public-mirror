package org.ICIQ.eChempad.entities;


/**
 * Enumerate object to store the different privileges that a user can have against arbitrary resources such as an
 * Experiment or Journal, in a comprehensive manner.
 */
public enum Role {
    NONE,
    READ,
    WRITE,
    EDIT,
    OWN,
    USER,
    ADMIN
}
