package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public interface GenericController <T extends IEntity, S extends Serializable> {

    /**
     * return the entity class of this generic repository.
     * Note: Default methods are a special Java 8 feature in where interfaces can define implementations for methods.
     * @return Internal class type of this generic repository, set at the creation of the repository.
     */
    public Class<T> getEntityClass();

    ResponseEntity<Set<T>> getAll();

    ResponseEntity<T> get(S id) throws ResourceNotExistsException;

    ResponseEntity<T> add(T t);

    ResponseEntity<T> remove(S id) throws ResourceNotExistsException, NotEnoughAuthorityException;

    ResponseEntity<T> put(T t, S id) throws ResourceNotExistsException, NotEnoughAuthorityException;


}
