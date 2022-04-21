/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.entities.Journal;

import java.util.UUID;

public interface ExperimentRepository extends GenericRepository<Experiment, UUID>{
}
