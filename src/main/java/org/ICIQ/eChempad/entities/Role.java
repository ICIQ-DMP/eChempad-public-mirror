/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.entities;

/**
 * Enumerate object to store the different roles that a user of the platform can have. They have to be considered as a
 * coarse-grained way of managing permissions. By being user or admin the application will limit which API calls are
 * available to us.
 */
public enum Role {
    USER,
    ADMIN
}