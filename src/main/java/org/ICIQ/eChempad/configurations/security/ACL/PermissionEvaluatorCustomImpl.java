/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.security.ACL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.logging.Logger;

@Component
class PermissionEvaluatorCustomImpl implements PermissionEvaluator {

    final
    PermissionEvaluator permissionEvaluator;

    @Autowired
    public PermissionEvaluatorCustomImpl(AclService aclService) {
        this.permissionEvaluator = new AclPermissionEvaluator(aclService);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        Logger.getGlobal().info("\nauth " + authentication + "\n targetObj " + targetDomainObject + "\npermission " + permission);
        return this.permissionEvaluator.hasPermission(authentication, targetDomainObject, permission);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        Logger.getGlobal().info("\nauth " + authentication + "\ntargetId " + targetId + "\ntargettype " + targetType + "\npermission " + permission);
        return this.permissionEvaluator.hasPermission(authentication, targetId, targetType, permission);
    }
}
