package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.Experiment;
import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.springframework.stereotype.Service;

@Service("entityConversionService")
public class EntityConversionServiceImpl implements EntityConversionService{

    @Override
    public Journal parseJournal(JPAEntity jpaEntity) {
        if (jpaEntity instanceof Journal)
        {
            return (Journal) jpaEntity;
        }

        Journal journal = new Journal();
        journal.setName(jpaEntity.getName());
        journal.setDescription(jpaEntity.getDescription());
        journal.setId(jpaEntity.getId());
        journal.setCreationDate(jpaEntity.getCreationDate());

        return journal;
    }

    @Override
    public Experiment parseExperiment(JPAEntity jpaEntity) {
        if (jpaEntity instanceof Experiment)
        {
            return (Experiment) jpaEntity;
        }

        Experiment experiment = new Experiment();
        experiment.setName(jpaEntity.getName());
        experiment.setDescription(jpaEntity.getDescription());
        experiment.setId(jpaEntity.getId());
        experiment.setCreationDate(jpaEntity.getCreationDate());

        return experiment;
    }

    @Override
    public Document parseDocument(JPAEntity jpaEntity) {
        if (jpaEntity instanceof Document)
        {
            return (Document) jpaEntity;
        }

        Document document = new Document();
        document.setName(jpaEntity.getName());
        document.setDescription(jpaEntity.getDescription());
        document.setId(jpaEntity.getId());
        document.setCreationDate(jpaEntity.getCreationDate());

        return document;
    }
}
