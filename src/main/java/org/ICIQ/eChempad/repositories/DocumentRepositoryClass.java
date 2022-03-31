/**
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.Document;
import org.ICIQ.eChempad.entities.Experiment;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.UUID;

@Repository
@Transactional
public class DocumentRepositoryClass extends GenericRepositoryClass<Document, UUID> implements DocumentRepository{
}
