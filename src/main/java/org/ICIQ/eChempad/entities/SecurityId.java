package org.ICIQ.eChempad.entities;

import javax.persistence.*;

/**
 * This class holds the information for a security identity (SID). The SID is used by the ACL submodules to identify a
 * role or user to determine if it is able to perform a certain operation against an entity.
 * <p>
 * Every user should have a corresponding SID to be able to manipulate the ACL tables of their owned identities.
 */
@Entity
public class SecurityId {

    /**
     * Identifier for the SID. Using Long as equivalent for big serial in PostgresSQL
     */
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Determines if the security ID references a user / identification or a role, which needs to be applied to a user
     */
    private boolean principal;

    /**
     * Name of the SID
     */
    private String sid;

    /**
     * Builds a SID in memory object.
     *
     * @param id Long, long unsigned int to identify a row in the table.
     * @param principal Determines if the SID belongs to a user (true) or to a role (false).
     * @param sid Name of the SID.
     */
    public SecurityId(Long id, boolean principal, String sid) {
        this.id = id;
        this.principal = principal;
        this.sid = sid;
    }

    public SecurityId() {}

    // GETTERS AND SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    @Override
    public String toString() {
        return "SecurityId{" +
                "id=" + id +
                ", principal=" + principal +
                ", sid='" + sid + '\'' +
                '}';
    }
}
