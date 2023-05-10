/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2023 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.wrappers;

import java.util.List;

public interface DataverseDatasetMetadata {

    void setTitle(String title);

    void setAuthorName(String authorName);

    void setAuthorAffiliation(String authorAffiliation);

    void setContactEmail(String contactEmail);

    void setDatasetContactName(String datasetContactName);

    void setDescription(String description);

    void setSubjects(List<String> categories);
}
