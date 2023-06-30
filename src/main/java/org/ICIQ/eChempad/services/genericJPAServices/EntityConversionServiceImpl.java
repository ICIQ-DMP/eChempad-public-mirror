/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2023 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.ICIQ.eChempad.entities.genericJPAEntities.DataEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.Entity;
import org.springframework.stereotype.Service;

@Service("entityConversionService")
public class EntityConversionServiceImpl implements EntityConversionService{

    @Override
    public Container parseJournal(DataEntity entity) {
        if (entity instanceof Container)
        {
            return (Container) entity;
        }

        Container container = new Container();
        container.setName(entity.getName());
        container.setDescription(entity.getDescription());
        container.setId(entity.getId());
        container.setCreationDate(entity.getCreationDate());

        return container;
    }

    @Override
    public Document parseDocument(DataEntity entity) {
        if (entity instanceof Document)
        {
            return (Document) entity;
        }

        Document document = new Document();
        document.setName(entity.getName());
        document.setDescription(entity.getDescription());
        document.setId(entity.getId());
        document.setCreationDate(entity.getCreationDate());

        return document;
    }
}
