package org.ICIQ.eChempad.repositories;

import java.util.UUID;

public interface ResearcherRepository {

    UUID getIdByEmail(String email);

    String getEmail(UUID id);

    String getFullName(UUID id);

    String getSignalsAPIKey(UUID id);
}
