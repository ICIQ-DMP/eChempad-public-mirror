package org.ICIQ.eChempad.configurations.Utilities;

import org.springframework.security.acls.model.Permission;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.acls.domain.AbstractPermission;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.core.parameters.P;

import javax.sql.DataSource;
import java.util.logging.Logger;

@Configuration
public class PermissionBuilder {

    private CumulativePermission permission;

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
        return new PermissionBuilder()
                .build(BasePermission.ADMINISTRATION)
                .build(BasePermission.CREATE)
                .build(BasePermission.DELETE)
                .build(BasePermission.READ)
                .build(BasePermission.WRITE)
                .getPermission();
    }
}
