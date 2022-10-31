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
