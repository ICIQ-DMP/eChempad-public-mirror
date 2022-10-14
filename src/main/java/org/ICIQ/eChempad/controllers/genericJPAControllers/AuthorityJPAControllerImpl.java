/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.controllers.genericJPAControllers;

import org.ICIQ.eChempad.entities.genericJPAEntities.Authority;
import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntityImpl;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.services.genericJPAServices.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/authority")
public class AuthorityJPAControllerImpl<T extends JPAEntityImpl, S extends Serializable> extends GenericJPAControllerImpl<Authority, UUID> implements AuthorityJPAController<Authority, UUID> {

    @Autowired
    public AuthorityJPAControllerImpl(AuthorityService<Authority, UUID> authorityService) {
        super(authorityService);
    }

    /**
     * The second arg of hasPermissions is what forbids with my current knowledge the existence of a generic remove
     * method, since jackson needs to know the type to be able to serialize / deserialize objects.
     * @param id Identifier of the Authority
     * @return Deleted Authority
     * @throws ResourceNotExistsException If the resource does not exist.
     * @throws NotEnoughAuthorityException If we cannot delete the resource because of permissions.
     */
    @DeleteMapping(
            value = "/{id}",
            produces = "application/json"
    )
    @PreAuthorize("hasPermission(#id, 'org.ICIQ.eChempad.entities.genericJPAEntities.Authority' , 'DELETE')")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public Authority remove(@PathVariable UUID id) throws ResourceNotExistsException, NotEnoughAuthorityException {
        Optional<Authority> entity = this.genericService.findById(id);
        if (entity.isPresent()) {
            this.genericService.deleteById(id);
            return entity.get();
        }
        else
            throw new ResourceNotExistsException();
    }
}
