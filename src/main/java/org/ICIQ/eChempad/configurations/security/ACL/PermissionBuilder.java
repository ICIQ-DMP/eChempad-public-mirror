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
package org.ICIQ.eChempad.configurations.security.ACL;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.acls.model.Permission;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.CumulativePermission;

import java.util.ArrayList;
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

    public static Permission getFullPermission()
    {
        PermissionBuilder permissionBuilder = new PermissionBuilder();
        for (Permission p: PermissionBuilder.availablePermissions) {
            permissionBuilder = permissionBuilder.build(p);
        }
        return permissionBuilder.getPermission();
    }

    public static List<Permission> getAllPermissions()
    {
        List<Permission> permissions = new ArrayList<>();
        for (Permission p: PermissionBuilder.availablePermissions) {
            permissions.add(p);
        }
        return permissions;
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
