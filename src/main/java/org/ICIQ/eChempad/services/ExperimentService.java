package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.exceptions.ExceptionResourceNotExists;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ExperimentService extends GenericService<Experiment, UUID> {


    //T saveOrUpdate(T entity);

    //T update(T entity, S id) throws ExceptionResourceNotExists;

    Set<Experiment> getAll();

    //T get(S id) throws ExceptionResourceNotExists;

    //void add(T entity);

    //void remove(S id) throws ExceptionResourceNotExists;

}
