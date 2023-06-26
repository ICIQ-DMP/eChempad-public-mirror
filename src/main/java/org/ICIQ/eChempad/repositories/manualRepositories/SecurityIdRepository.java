package org.ICIQ.eChempad.repositories.manualRepositories;

import org.ICIQ.eChempad.entities.SecurityId;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * This class is used to manipulate the SID ACL table. Note that this is not a JPA repository, so the associated table
 * is not created automatically. The table is expected to be created with the ACL SQL schema in the resources folder of
 * the project. We need to be able to add (and remove) SIDs from this table manually in order to allow users to obtain
 * its dedicated SID to be identified, so they can use the ACL services.
 */
@org.springframework.stereotype.Repository
public interface SecurityIdRepository extends CrudRepository<SecurityId, Long> {

    /**
     * Returns all SecurityId stored in the database.
     *
     * @return List of SecurityIDs.
     */
    @Query(value = "SELECT * FROM acl_sid", nativeQuery = true)
    @NotNull
    Iterable<SecurityId> findAll();

    /**
     * Returns all the SecurityIds stored in the database that match at least one of the supplied SecurityId.
     *
     * @param ids Set of ids that would be matched against the ids in the database.
     * @return List of SecurityIds that matched any of the supplied ids.
     */
    @Query(value = "SELECT * FROM acl_sid WHERE id IN :ids", nativeQuery = true)
    @NotNull
    Iterable<SecurityId> findAllById(@Param("ids") @NotNull Iterable<Long> ids);

    /**
     * Saves a Security ID record in the database.
     *
     * @param sid SecurityId to be saved.
     * @return Saved security ID.
     */
    @Modifying
    @Query(value = "INSERT INTO acl_sid VALUES (:sid)", nativeQuery = true)
    int save(@Param("sid") @NotNull Long sid);

    /**
     * Save all security IDs into the database.
     *
     * @param sids Security IDs to be saved.
     * @return returns the saved Security IDs.
     */

    @Modifying
    @Query(value = "INSERT INTO acl_sid VALUES (:sids)", nativeQuery = true)
    <S extends SecurityId> @NotNull Iterable<S> saveAll(@Param("sids") @NotNull Iterable<S> sids);

    /**
     * Delete all Security IDs that match the supplied list of SIDs.
     *
     * @param ids Iterable of IDs that have to be deleted if present.
     */
    @Modifying
    @Query(value = "DELETE FROM acl_sid WHERE id IN (:ids)", nativeQuery = true)
    void deleteAllByIdInBatch(Iterable<SecurityId> ids);

    /**
     * Delete all records in the sid table.
     */
    @Modifying
    @Query(value = "DELETE FROM acl_sid WHERE true", nativeQuery = true)  // Added WHERE clause to avoid warning
    void deleteAllInBatch();

    /**
     * Obtain the Security ID that matches the supplied id.
     *
     * @param id The ID that has to be matched to retrieve the data.
     * @return SecurityID that has the supplied id.
     */

    @Query(value = "SELECT * from acl_sid WHERE id=:id", nativeQuery = true)  // Added WHERE clause to avoid warning
    SecurityId getById(@Param("id") Long id);
}
