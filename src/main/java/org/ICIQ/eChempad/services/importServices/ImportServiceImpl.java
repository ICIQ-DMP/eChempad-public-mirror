package org.ICIQ.eChempad.services.importServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.ICIQ.eChempad.entities.genericJPAEntities.DataEntity;

import java.util.List;

public abstract class ImportServiceImpl implements ImportService{

    @Override
    public void updateRootContainers(String APIKey) {
        List<DataEntity> rootEntities = this.readRootEntities(APIKey);
        for (DataEntity dataEntity : rootEntities)
        {
            this.updateRootContainer((Container) dataEntity, APIKey);
        }
    }
}
