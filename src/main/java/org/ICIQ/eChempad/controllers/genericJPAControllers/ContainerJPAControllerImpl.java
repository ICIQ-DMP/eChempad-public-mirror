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
package org.ICIQ.eChempad.controllers.genericJPAControllers;

import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.ICIQ.eChempad.entities.genericJPAEntities.EntityImpl;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.services.genericJPAServices.ContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/journal")
public class ContainerJPAControllerImpl<T extends EntityImpl, S extends Serializable> extends GenericJPAControllerImpl<Container, UUID> implements ContainerJPAController<Container, UUID> {

    @Autowired
    public ContainerJPAControllerImpl(ContainerService<Container, UUID> containerService) {
        super(containerService);
    }

    /**
     * The second arg of hasPermissions is what forbids with my current knowledge the existence of a generic remove
     * method, since jackson needs to know the type to be able to serialize / deserialize objects.
     * @param id Identifier of the Journal
     * @return Deleted Journal
     * @throws ResourceNotExistsException If the resource does not exist.
     * @throws NotEnoughAuthorityException If we cannot delete the resource because of permissions.
     */
    @DeleteMapping(
            value = "/{id}",
            produces = "application/json"
    )
    @PreAuthorize("hasPermission(#id, 'org.ICIQ.eChempad.entities.genericJPAEntities.Journal', 'DELETE')")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public Container remove(@PathVariable UUID id) throws ResourceNotExistsException, NotEnoughAuthorityException {
        Optional<Container> entity = this.genericService.findById(id);
        if (entity.isPresent()) {
            this.genericService.deleteById(id);
            return entity.get();
        }
        else
            throw new ResourceNotExistsException();
    }
}
