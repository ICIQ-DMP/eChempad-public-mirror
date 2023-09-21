package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.UpdateState;
import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.ICIQ.eChempad.entities.genericJPAEntities.DataEntity;
import org.ICIQ.eChempad.services.genericJPAServices.ContainerService;
import org.ICIQ.eChempad.services.genericJPAServices.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class DataEntityUpdateStateServiceImpl implements DataEntityUpdateStateService{

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    ContainerService<Container, UUID> containerService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    DocumentService<Container, UUID> documentService;

    @Override
    public int compareDates(Date database, Date importing) {
        return database.compareTo(importing);
    }

    @Override
    public int compareDates(String database, String importing) {
        return 0;
    }

    @Override
    public UpdateState compareEntities(DataEntity database, DataEntity importing) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        if (database == null)
        {
            return UpdateState.NOT_PRESENT;
        }

        // Entity in eChempad did not suffer any change since its import
        if (simpleDateFormat.format(database.getLastEditionDate()).equals(simpleDateFormat.format(database.getCreationDate())))
        {
            // Entity in Signals has not changed since its import
            if (simpleDateFormat.format(database.getOriginLastEditionDate()).equals(simpleDateFormat.format(importing.getOriginLastEditionDate())))
            {
                return UpdateState.UP_TO_DATE;
            }
            else
            {
                Logger.getGlobal().warning("DB origin last edition date " + simpleDateFormat.format(database.getOriginLastEditionDate()) + " Importing origin last edition date " + simpleDateFormat.format(importing.getOriginLastEditionDate()));
                return UpdateState.ORIGIN_HAS_CHANGES;
            }
        }
        else  // Entity in eChempad has been changed since its import
        {
            // Entity in Signals has not changed since its import
            if (simpleDateFormat.format(database.getOriginLastEditionDate()).equals(simpleDateFormat.format(importing.getOriginLastEditionDate())))
            {
                return UpdateState.ECHEMPAD_HAS_CHANGES;
            }
            else
            {
                return UpdateState.BOTH_HAVE_CHANGES;
            }
        }
    }
}
