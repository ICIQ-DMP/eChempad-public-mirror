package org.ICIQ.eChempad.entities;

import java.io.Serializable;
import java.util.UUID;

/**
 * Used to limit the generic inheritance to entities that comply with the contract of being able of manipulating 
 * its uuid
 */
public interface IEntity {
    UUID getUUid();
    void setUUid(UUID id);
}
