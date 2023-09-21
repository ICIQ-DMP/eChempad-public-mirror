/*
 * eChempad is a suite of web services oriented to manage the entire
 * data life-cycle of experiments and assays from Experimental
 * Chemistry and related Science disciplines.
 *
 * Copyright (C) 2021 - present Institut Català d'Investigació Química (ICIQ)
 *
 * eChempad is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.ICIQ.eChempad.entities.genericJPAEntities.DataEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
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
