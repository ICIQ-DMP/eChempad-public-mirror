package org.ICIQ.eChempad.entities;

import org.ICIQ.eChempad.entities.genericJPAEntities.Entity;

import javax.persistence.*;
import java.io.Serializable;

@javax.persistence.Entity
@Table(
        name = "acl_sid",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id"})
        })
public class SecurityId implements Serializable, Entity {
    @Column(unique = true)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "principal")
    private boolean principal;

    @Column(name = "sid")
    private String sid;


    public SecurityId() {
    }

    public SecurityId(Long id, boolean principal, String sid) {
        this.id = id;
        this.principal = principal;
        this.sid = sid;
    }

    public SecurityId(boolean principal, String sid) {
        this.principal = principal;
        this.sid = sid;
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

    /**
     * Exposes and returns the UUID of an entity.
     *
     * @return UUID of the entity.
     */
    @Override
    public Serializable getId() {
        return this.id;
    }

    /**
     * Sets the UUID of an entity.
     * This is a method that will have collisions with hibernate because hibernate uses the id field as a PK
     * (Primary Key) for accessing the database. As such, this method has to be only used against entities that are
     * not managed by hibernate.
     * This interface is specially designed to expose this specific method of all the entities, and is specially
     * designed to perform updates of existing entities of the database when an ID is not supplied with the received
     * data object.
     *
     * @param id ID that will be set. Only usable on dettached spring boot instances
     */
    @Override
    public void setId(Serializable id) {
        this.id = (Long) id;
    }

    /**
     * Implemented by every class to return its own type.
     *
     * @return Class of the object implementing this interface.
     */
    @Override
    public <T extends Entity> Class<T> getType() {
        return (Class<T>) this.getClass();
    }

    /**
     * Obtains the typeName, used by jackson to deserialize generics.
     *
     * @return Name of the class as string.
     */
    @Override
    public String getTypeName() {
        return this.getType().getCanonicalName();
    }
}