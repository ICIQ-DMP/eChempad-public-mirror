package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepositoryInterface extends CrudRepository<User, UUID> {


}
