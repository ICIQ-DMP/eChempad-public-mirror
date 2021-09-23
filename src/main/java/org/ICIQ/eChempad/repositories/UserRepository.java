package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository {

    UUID getIdByEmail(String email);

    String getEmail(UUID id);

    String getFullName(UUID id);

    String getSignalsAPIKey(UUID id);

}
