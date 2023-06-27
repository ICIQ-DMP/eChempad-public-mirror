/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2023 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.security.ACL;

import org.ICIQ.eChempad.entities.genericJPAEntities.DataEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Entity;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
 * <p>
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
     * Receives an entity whose permissions have to be modified with the supplied Permission object. Then, obtains the
     * authentication of SecurityContextHolder (implicit parameter) and depending on its type performs a conversion to
     * obtain the username in String format.
     */
    public void addPermissionToUserInEntity(DataEntity entity, Permission permission) {
        // Obtain principal object. It could be a normal UserDetails authentication or the String of a user if we are
        // using this function manually
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = null;
        if (principal instanceof String)
        {
            username = (String) principal;
        }
        else if (principal instanceof UserDetails)
        {
            username = ((UserDetails) principal).getUsername();
        }
        else
        {
            Logger.getGlobal().warning("In func addPermissionToUserInEntity the security context is: " + principal.toString());
            // TODO throw exception
            return;
        }

        this.addPermissionToEntity(entity,true, permission, username);
    }

    @Transactional
    public void addPermissionToEntity(Entity entity, boolean inheriting, Permission permission, String username)
    {
        // Obtain the identity of the object by using its class and its id
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(entity.getType(), entity.getId());

        // Obtain the identity of the user
        Sid sid;
        if (username == null) {  // If not received obtain in from SecurityContextHolder
            // If we do not receive a userDetails, obtain it from the security context
            Object userPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            String usernameDeduced;
            if (userPrincipal instanceof UserDetails)
            {
                usernameDeduced = ((UserDetails) userPrincipal).getUsername();
            }
            else if (userPrincipal instanceof String)
            {
                usernameDeduced = (String) userPrincipal;
            }
            else
            {
                usernameDeduced = "";
                Logger.getGlobal().warning("Username deduced from context is empty!");
            }
            sid = new PrincipalSid(usernameDeduced);
        }
        else {
            sid = new PrincipalSid(username);
        }

        // Create or update the relevant ACL
        MutableAcl acl;
        Logger.getGlobal().warning("normal; type: " + objectIdentity.getType() + " id: " + objectIdentity.getIdentifier());

        try {
            // Logger.getGlobal().warning("The object identity is " + objectIdentity.toString());
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

        // If inheriting is true then set entries inheriting and build the ACL of the parent
        if (inheriting && entity instanceof DataEntity)
        {
            acl.setEntriesInheriting(true);

            Logger.getGlobal().warning("Inheriting; type: " + ((DataEntity)entity).getParent().getType() + " id: " + ((DataEntity)entity).getParent().getId());

            // Construct identity of parent object
            ObjectIdentity objectIdentity_parent = new ObjectIdentityImpl(((DataEntity)entity).getParent().getType(), ((DataEntity)entity).getParent().getId());

            // Retrieve ACL of parent object
            MutableAcl acl_parent;
            try {
                acl_parent = (MutableAcl) this.aclService.readAclById(objectIdentity_parent);
            } catch (NotFoundException nfe) {
                acl_parent = this.aclService.createAcl(objectIdentity_parent);
            }
            acl.setParent(acl_parent);
        }

        // Now grant some permissions via an access control entry (ACE)
        acl.insertAce(acl.getEntries().size(), setPermission, sid, true);
        aclService.updateAcl(acl);
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
