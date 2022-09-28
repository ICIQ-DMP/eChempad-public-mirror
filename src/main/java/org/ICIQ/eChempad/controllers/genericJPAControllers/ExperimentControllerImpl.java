package org.ICIQ.eChempad.controllers.genericJPAControllers;

import org.ICIQ.eChempad.entities.genericJPAEntities.Authority;
import org.ICIQ.eChempad.entities.genericJPAEntities.Experiment;
import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntityImpl;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.services.genericJPAServices.AuthorityService;
import org.ICIQ.eChempad.services.genericJPAServices.ExperimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/experiment")
public class ExperimentControllerImpl<T extends JPAEntityImpl, S extends Serializable> extends GenericControllerImpl<Experiment, UUID> implements ExperimentController<Experiment, UUID> {

    @Autowired
    public ExperimentControllerImpl(ExperimentService<Experiment, UUID> experimentService) {
        super(experimentService);
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
    @PreAuthorize("hasPermission(#id, 'org.ICIQ.eChempad.entities.genericJPAEntities.Experiment' , 'DELETE')")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public Experiment remove(@PathVariable UUID id) throws ResourceNotExistsException, NotEnoughAuthorityException {
        Optional<Experiment> entity = this.genericService.findById(id);
        if (entity.isPresent()) {
            this.genericService.deleteById(id);
            return entity.get();
        }
        else
            throw new ResourceNotExistsException();
    }

    /**
     * Adds an experiment to a certain journal if we have enough permissions
     *
     * @param experiment   data of the new experiment.
     * @param journal_uuid UUID of the journal we are adding.
     * @throws ResourceNotExistsException  Thrown if the referred journal does not exist in the DB
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to write into this journal.
     */
    @PostMapping(
            value = "/{journal_uuid}/journal",
            consumes = "application/json"
    )
    @PreAuthorize("hasPermission(#journal_uuid, 'org.ICIQ.eChempad.entities.genericJPAEntities.Journal' , 'WRITE')")
    @Override
    public Experiment addExperimentToJournal(@Validated @RequestBody Experiment experiment, @PathVariable UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        return ((ExperimentService<JPAEntityImpl, Serializable>) this.genericService).addExperimentToJournal(experiment, journal_uuid);
    }

    /**
     * Gets all the experiments belonging to a certain journal.
     *
     * @param journal_uuid UUID of the journal we are querying
     * @return returns all experiments inside the journal if they are readable by the logged user.
     * @throws ResourceNotExistsException  Thrown if the referred journal does not exist in the DB
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to read into this journal.
     */
    @GetMapping(
            value = "/{journal_uuid}/journal",
            produces = "application/json"
    )
    @PreAuthorize("hasPermission(#journal_uuid, 'org.ICIQ.eChempad.entities.genericJPAEntities.Journal' , 'READ')")
    @Override
    public Set<Experiment> getExperimentsFromJournal(@PathVariable UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        return ((ExperimentService<JPAEntityImpl, Serializable>) this.genericService).getExperimentsFromJournal(journal_uuid);
    }
}
