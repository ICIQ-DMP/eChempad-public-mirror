/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.security.ACL;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.acls.model.Permission;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.CumulativePermission;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Configuration
public class PermissionBuilder{

    private CumulativePermission permission;
    private static final List<Permission> availablePermissions = Arrays.asList(
            BasePermission.READ,
            BasePermission.WRITE,
            BasePermission.CREATE,
            BasePermission.DELETE,
            BasePermission.ADMINISTRATION);

    public PermissionBuilder(CumulativePermission permission) {
        this.permission = permission;
    }

    public PermissionBuilder() {
        this.permission = new CumulativePermission();
    }

    public CumulativePermission getPermission() {
        return permission;
    }

    @Override
    public String toString() {
        return "PermissionBuilder{" +
                "permission=" + permission +
                '}';
    }

    public PermissionBuilder build(Permission... a) {
        for (Permission basePermission : a) {
            this.permission.set(basePermission);
        }
        return this;
    }

    public PermissionBuilder build(Permission a) {
        this.permission.set(a);
        return this;
    }

    public static Permission getFullPermissions()
    {
        PermissionBuilder permissionBuilder = new PermissionBuilder();
        for (Permission p: PermissionBuilder.availablePermissions) {
            permissionBuilder = permissionBuilder.build(p);
        }
        return permissionBuilder.getPermission();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @NotNull
    public static Iterator<Permission> getFullPermissionsIterator() {
        return PermissionBuilder.availablePermissions.iterator();
    }
}
