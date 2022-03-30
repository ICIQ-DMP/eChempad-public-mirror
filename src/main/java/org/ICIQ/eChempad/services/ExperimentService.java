package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;

import java.util.Set;
import java.util.UUID;

public interface ExperimentService extends GenericService<Experiment, UUID> {


    /**
     * Adds a new experiment into a certain journal, provided as a URL parameter.
     * @param experiment Data of the addition of the experiment.
     * @param journal_uuid Journal where er are adding the new experiment. We must have WRITE permissions
     * @throws ResourceNotExistsException The resource does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have the required authority to perform the operation in the journal
     */
    void addExperimentToJournal(Experiment experiment, UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;


    /**
     * gets the experiments of a certain journal if we have enough privileges to do so.
     * @param journal_uuid UUID of the journal we are referring
     * @return Returns all the experiments under this journal if they exist.
     * @throws ResourceNotExistsException Thrown if this journal does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to read the journal.
     */
    Set<Experiment> getExperimentsFromJournal(UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

    //T saveOrUpdate(T entity);

    //T update(T entity, S id) throws ExceptionResourceNotExists;

    Set<Experiment> getAll();

    //T get(S id) throws ExceptionResourceNotExists;

    //void add(T entity);

    //void remove(S id) throws ExceptionResourceNotExists;

}
