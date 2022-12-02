/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.security.ACL;

import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 14/10/2022
 *
 * Wraps the {@code AclService} provided by Spring, and adds some decoration calls in order to ease the use of the ACL
 * infrastructure. This class forwards all the calls to it to an instance of {@code MutableAclService} contained in the
 * instance.
 */
@Repository
@Transactional
public class AclServiceCustomImpl implements AclService{

    /**
     * Spring provided {@code MutableAclService} that can perform all the calls that this class receives.
     */
    private final MutableAclService aclService;

    public AclServiceCustomImpl(MutableAclService aclService) {
        this.aclService = aclService;
    }

    /**
     * We assume that the security context is full
     */
    public void addPermissionToUserInEntity(JPAEntity JPAEntity, Permission permission) {
        // Obtain principal object. It could be a normal UserDetails authentication or the String of a user if we are
        // using this function manually
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof String)
        {
            this.addPermissionToUserInEntity(JPAEntity, permission, (String) principal);
        }
        else if (principal instanceof UserDetails)
        {
            this.addPermissionToUserInEntity(JPAEntity, permission, (UserDetails) principal);
        }
        else
        {
            // TODO throw exception
            Logger.getGlobal().warning("In func addPermissionToUserInEntity the security context is: " + principal.toString());
        }
    }

    // TODO refactor methods, so only one big private method is exposed and the rest are just decorators to that method.
    @Transactional
    public void addPermissionToUserInEntity(JPAEntity JPAEntity, Permission permission, String userName)
    {
        Logger.getGlobal().info("ACL class ENTITY TYPE: " + JPAEntity.getTypeName());
        // Obtain the identity of the object by using its class and its id
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(JPAEntity.getType(), JPAEntity.getId());

        // Obtain the identity of the user
        Sid sid;
        if (userName == null) {
            // If we do not receive a userDetails, obtain it from the security context
            UserDetails u = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            sid = new PrincipalSid(u.getUsername());
        }
        else {
            sid = new PrincipalSid(userName);
        }

        // Create or update the relevant ACL
        MutableAcl acl;
        try {
            acl = (MutableAcl) aclService.readAclById(objectIdentity);
        } catch (NotFoundException nfe) {
            acl = aclService.createAcl(objectIdentity);
        }

        // Set administration permission if not received
        Permission setPermission;
        if (permission == null) {
            setPermission = BasePermission.ADMINISTRATION;
        }
        else {
            setPermission = permission;
        }

        // Now grant some permissions via an access control entry (ACE)
        acl.insertAce(acl.getEntries().size(), setPermission, sid, true);
        aclService.updateAcl(acl);
    }

    @Transactional
    public void addAllPermissionToLoggedUserInEntity(JPAEntity JPAEntity, boolean inheriting, JPAEntity parentEntity, Class<?> theClass)
    {
        // parentEntity is lazily loaded. It only has loaded its ID! If we try to use other fields, an implicit proxy
        // initialization will be triggered in order to retrieve the full object from DB, and the method will fail
        // because we are outside transactional boundaries

        // Obtain the identity of the object by using its class and its id
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(JPAEntity.getType(), JPAEntity.getId());

        // Obtain the identity of the user
        UserDetails u = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Sid sid = new PrincipalSid(u.getUsername());

        // Create or update the relevant ACL
        MutableAcl acl;
        try {
            acl = (MutableAcl) this.aclService.readAclById(objectIdentity);
        } catch (NotFoundException nfe) {
            acl = this.aclService.createAcl(objectIdentity);
        }

        // Now grant all permissions via an access control entry (ACE)
        Iterator<Permission> it = PermissionBuilder.getFullPermissionsIterator();
        while (it.hasNext())
        {
            acl.insertAce(acl.getEntries().size(), it.next(), sid, true);
        }

        if (inheriting)
        {
            acl.setEntriesInheriting(true);

            // Construct identity of parent object
            ObjectIdentity objectIdentity_parent = new ObjectIdentityImpl(theClass, parentEntity.getId());

            // Retrieve ACL of parent object
            MutableAcl acl_parent;
            try {
                acl_parent = (MutableAcl) this.aclService.readAclById(objectIdentity_parent);
            } catch (NotFoundException nfe) {
                acl_parent = this.aclService.createAcl(objectIdentity_parent);
            }
            acl.setParent(acl_parent);
        }

        this.aclService.updateAcl(acl);
    }

    /**
     * We assume that the security context is full
     */
    public void addPermissionToUserInEntity(JPAEntity JPAEntity, Permission permission, UserDetails userDetails)
    {
        this.addPermissionToUserInEntity(JPAEntity, permission, userDetails.getUsername());
    }


    /*
     * DELEGATED METHODS
     */

    /**
     * Creates an empty <code>Acl</code> object in the database. It will have no entries.
     * The returned object will then be used to add entries.
     *
     * @param objectIdentity the object identity to create
     * @return an ACL object with its ID set
     * @throws AlreadyExistsException if the passed object identity already has a record
     */
    public MutableAcl createAcl(ObjectIdentity objectIdentity) throws AlreadyExistsException {
        return aclService.createAcl(objectIdentity);
    }

    /**
     * Removes the specified entry from the database.
     *
     * @param objectIdentity the object identity to remove.
     * @param deleteChildren Whether to cascade delete to children.
     * @throws ChildrenExistException if the deleteChildren argument was
     * <code>false</code> but children exist
     */
    public void deleteAcl(ObjectIdentity objectIdentity, boolean deleteChildren) throws ChildrenExistException {
        aclService.deleteAcl(objectIdentity, deleteChildren);
    }

    /**
     * Changes an existing <code>Acl</code> in the database.
     *
     * @param acl to modify
     * @throws NotFoundException if the relevant record could not be found (did you
     * remember to use {@link #createAcl(ObjectIdentity)} to create the object, rather
     * than creating it with the <code>new</code> keyword?)
     */
    public MutableAcl updateAcl(MutableAcl acl) throws NotFoundException {
        return aclService.updateAcl(acl);
    }

    /**
     * Locates all object identities that use the specified parent. This is useful for
     * administration tools.
     *
     * @param parentIdentity to locate children of
     * @return the children (or <tt>null</tt> if none were found)
     */
    public List<ObjectIdentity> findChildren(ObjectIdentity parentIdentity) {
        return aclService.findChildren(parentIdentity);
    }

    /**
     * Same as {@link #readAclsById(List)} except it returns only a single Acl.
     * <p>
     * This method should not be called as it does not leverage the underlying
     * implementation's potential ability to filter <tt>Acl</tt> entries based on a
     * {@link Sid} parameter.
     * </p>
     * @param object to locate an {@link Acl} for
     * @return the {@link Acl} for the requested {@link ObjectIdentity} (never
     * <tt>null</tt>)
     * @throws NotFoundException if an {@link Acl} was not found for the requested
     * {@link ObjectIdentity}
     */
    public Acl readAclById(ObjectIdentity object) throws NotFoundException {
        return aclService.readAclById(object);
    }

    /**
     * Same as {@link #readAclsById(List, List)} except it returns only a single Acl.
     *
     * @param object to locate an {@link Acl} for
     * @param sids the security identities for which {@link Acl} information is required
     * (may be <tt>null</tt> to denote all entries)
     * @return the {@link Acl} for the requested {@link ObjectIdentity} (never
     * <tt>null</tt>)
     * @throws NotFoundException if an {@link Acl} was not found for the requested
     * {@link ObjectIdentity}
     */
    public Acl readAclById(ObjectIdentity object, List<Sid> sids) throws NotFoundException {
        return aclService.readAclById(object, sids);
    }

    /**
     * Obtains all the <tt>Acl</tt>s that apply for the passed <tt>Object</tt>s.
     * <p>
     * The returned map is keyed on the passed objects, with the values being the
     * <tt>Acl</tt> instances. Any unknown objects will not have a map key.
     *
     * @param objects the objects to find {@link Acl} information for
     * @return a map with exactly one element for each {@link ObjectIdentity} passed as an
     * argument (never <tt>null</tt>)
     * @throws NotFoundException if an {@link Acl} was not found for each requested
     * {@link ObjectIdentity}
     */
    public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects) throws NotFoundException {
        return aclService.readAclsById(objects);
    }

    /**
     * Obtains all the <tt>Acl</tt>s that apply for the passed <tt>Object</tt>s, but only
     * for the security identifies passed.
     * <p>
     * Implementations <em>MAY</em> provide a subset of the ACLs via this method although
     * this is NOT a requirement. This is intended to allow performance optimisations
     * within implementations. Callers should therefore use this method in preference to
     * the alternative overloaded version which does not have performance optimisation
     * opportunities.
     * <p>
     * The returned map is keyed on the passed objects, with the values being the
     * <tt>Acl</tt> instances. Any unknown objects (or objects for which the interested
     * <tt>Sid</tt>s do not have entries) will not have a map key.
     *
     * @param objects the objects to find {@link Acl} information for
     * @param sids the security identities for which {@link Acl} information is required
     * (may be <tt>null</tt> to denote all entries)
     * @return a map with exactly one element for each {@link ObjectIdentity} passed as an
     * argument (never <tt>null</tt>)
     * @throws NotFoundException if an {@link Acl} was not found for each requested
     * {@link ObjectIdentity}
     */
    public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects, List<Sid> sids) throws NotFoundException {
        return aclService.readAclsById(objects, sids);
    }
}
