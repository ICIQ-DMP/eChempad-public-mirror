package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.UpdateState;
import org.ICIQ.eChempad.entities.genericJPAEntities.DataEntity;

import java.util.Date;

/**
 * This class models the logic to provide the service to compare two DataEntity instances that actually represent the
 * same entity, but that could be in different versions of this same entity. The first one most of the time will be an
 * entity in our database and the second one will be a detached entity that we are trying to import.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 28/08/2023
 **/

public interface DataEntityUpdateStateService {

    /**
     * Compare two dates. Returns >0 if the importing date is older than the database date, returns 0 if they are equal
     * and returns <0 if the importing date is newer than the database date.
     *
     * @param database A Date object that most of the time will be coming from the database.
     * @param importing A Date object that most of the time will be coming from an external service.
     * @return An int representing the differential state between the two objects.
     */
    int compareDates(Date database, Date importing);

    /**
     * Decorator for the compareDates(Date, Date) method. Parses the dates from the supplied Strings to Date objects and
     * calls compareDates(Date, Date) to perform the real logic.
     *
     * @param database A String object expected to represent a Date that most of the time will be coming from the
     *                 database.
     * @param importing A String object expected to represent a Date that most of the time will be coming from an
     *                  external service.
     * @return An int representing the differential state between the two objects.
     */
    int compareDates(String database, String importing);

    /**
     * Returns the state of update between two DataEntity objects that actually represent the same element but that are
     * in different versions.
     *
     * @see UpdateState
     *
     * The UpdateState will be chosen with the following algorithm:
     * - NOT_PRESENT: Returned only if the database entity is null.
     * - UP_TO_DATE: Returned if database.modificationDate == database.creationDate && database.originModificationDat
     *               == importing.originModificationDate
     * - ECHEMPAD_HAS_CHANGES: Returned if database.modificationDate != database.creationDate &&
     *                         database.originModificationDate == importing.originModificationDate
     * - ORIGIN_HAS_CHANGES: Returned if database.modificationDate == database.creationDate &&
     *                       database.originModificationDate != importing.originModificationDate
     * - BOTH_HAVE_CHANGES: Returned if database.modificationDate != database.creationDate &&
     *                      database.originModificationDate != importing.originModificationDate
     *
     * @param database DataEntity that most of the time will be coming from the database.
     * @param importing DataEntity that most of the time will be coming from an importation service.
     * @return UpdateState object with the differential state between the two DataEntity objects.
     */
    UpdateState compareEntities(DataEntity database, DataEntity importing);
}
