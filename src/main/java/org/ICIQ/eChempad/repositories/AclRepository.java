package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.Researcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.UUID;

@Repository
public class AclRepository {

    @Autowired
    private MutableAclService aclService;


    /**
     * We assume that the security context is full
     */
    public void addGenericAclPermissions(Class<?> theType, Permission permission)
    {
        // ACL code
        // Prepare the information we'd like in our access control entry (ACE)
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(theType, (Serializable) UUID.randomUUID());

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Sid sid = new PrincipalSid(userDetails.getUsername());

        // Create or update the relevant ACL
        MutableAcl acl = null;
        try {
            acl = (MutableAcl) aclService.readAclById(objectIdentity);
        } catch (NotFoundException nfe) {
            acl = aclService.createAcl(objectIdentity);
        }

        // Now grant some permissions via an access control entry (ACE)
        acl.insertAce(acl.getEntries().size(), permission, sid, true);
        aclService.updateAcl(acl);
    }
}
