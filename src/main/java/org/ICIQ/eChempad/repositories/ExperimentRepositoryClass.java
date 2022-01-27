package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.entities.Journal;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.UUID;

@Repository
@Transactional
public class ExperimentRepositoryClass extends GenericRepositoryClass<Experiment, UUID> implements ExperimentRepository{
}
