package org.ICIQ.eChempad.configurations.Helpers;

import org.ICIQ.eChempad.entities.IEntity;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Repository
public class AclRepositoryImpl {

    @Autowired
    private MutableAclService aclService;


    /**
     * We assume that the security context is full
     */
    public void addPermissionToUserInEntity(IEntity entity, Permission permission, String userName)
    {
        Logger.getGlobal().info("entity: " + entity + "permissions: " + permission + "user " + userName);
        // Obtain the identity of the object by using its class and its id
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(entity.getMyType(), entity.getId());

        Logger.getGlobal().info("object identity " + objectIdentity);

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

        Logger.getGlobal().info("security identity " + sid);

        // Create or update the relevant ACL
        MutableAcl acl;
        try {
            acl = (MutableAcl) aclService.readAclById(objectIdentity);
        } catch (NotFoundException nfe) {
            acl = aclService.createAcl(objectIdentity);
        }

        Logger.getGlobal().info("Obtained acl " + acl);

        // Set administration permission if not received
        Permission setPermission;
        if (permission == null) {
            setPermission = BasePermission.ADMINISTRATION;
        }
        else {
            setPermission = permission;
        }

        // Now grant some permissions via an access control entry (ACE)
        Logger.getGlobal().info("the acl" + acl.toString());
        acl.insertAce(acl.getEntries().size(), setPermission, sid, true);
        aclService.updateAcl(acl);
    }

    /**
     * We assume that the security context is full
     */
    public void addPermissionToUserInEntity(IEntity entity, Permission permission, UserDetails userDetails)
    {
        this.addPermissionToUserInEntity(entity, permission, userDetails.getUsername());
    }

    /**
     * We assume that the security context is full
     */
    public void addPermissionToUserInEntity(IEntity entity, Permission permission)
    {
        this.addPermissionToUserInEntity(entity, permission, (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

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
        MutableAcl acl;
        try {
            acl = (MutableAcl) aclService.readAclById(objectIdentity);
        } catch (NotFoundException nfe) {
            acl = aclService.createAcl(objectIdentity);
        }

        // Now grant some permissions via an access control entry (ACE)
        acl.insertAce(acl.getEntries().size(), permission, sid, true);
        aclService.updateAcl(acl);
    }


    // Delegated methods

    /**
     * Creates an empty <code>Acl</code> object in the database. It will have no entries.
     * The returned object will then be used to add entries.
     * @param objectIdentity the object identity to create
     * @return an ACL object with its ID set
     * @throws AlreadyExistsException if the passed object identity already has a record
     */
    public MutableAcl createAcl(ObjectIdentity objectIdentity) throws AlreadyExistsException {
        return aclService.createAcl(objectIdentity);
    }

    /**
     * Removes the specified entry from the database.
     * @param objectIdentity the object identity to remove
     * @param deleteChildren whether to cascade the delete to children
     * @throws ChildrenExistException if the deleteChildren argument was
     * <code>false</code> but children exist
     */
    public void deleteAcl(ObjectIdentity objectIdentity, boolean deleteChildren) throws ChildrenExistException {
        aclService.deleteAcl(objectIdentity, deleteChildren);
    }

    /**
     * Changes an existing <code>Acl</code> in the database.
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
     * @param object to locate an {@link Acl} for
     * @param sids the security identities for which {@link Acl} information is required
     * (may be <tt>null</tt> to denote all entries)
     * @return the {@link Acl} for the requested {@link ObjectIdentity} (never
     * <tt>null</tt>)
     * @throws NotFoundException if an {@link Acl} was not found for the requested
     * {@link ObjectIdentity}
     */
    public Acl readAclById(ObjectIdentity object, List<Sid> sids) throws NotFoundException {
        MutableAcl acl = (MutableAcl) aclService.readAclById(object, sids);


        System.out.println(acl.getEntries());
        return acl;
    }

    /**
     * Obtains all the <tt>Acl</tt>s that apply for the passed <tt>Object</tt>s.
     * <p>
     * The returned map is keyed on the passed objects, with the values being the
     * <tt>Acl</tt> instances. Any unknown objects will not have a map key.
     * </p>
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
     * </p>
     * <p>
     * The returned map is keyed on the passed objects, with the values being the
     * <tt>Acl</tt> instances. Any unknown objects (or objects for which the interested
     * <tt>Sid</tt>s do not have entries) will not have a map key.
     * </p>
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
