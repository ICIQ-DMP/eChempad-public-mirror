/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2023 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.entities;

import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.ICIQ.eChempad.entities.genericJPAEntities.Entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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
        this.initCreationDate();
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


    @Override
    public Serializable getId() {
        return this.id;
    }


    @Override
    public void setId(Serializable id) {
        this.id = (Long) id;
    }


    @Override
    public <T extends Entity> Class<T> getType() {
        return (Class<T>) this.getClass();
    }


    @Override
    public String getTypeName() {
        return this.getType().getCanonicalName();
    }

    @Override
    public void initCreationDate() {

    }

    @Override
    public Container getParent() {
        return null;
    }

    @Override
    public void setParent(Container parent) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void setDescription(String description) {

    }

    @Override
    public Date getCreationDate() {
        return null;
    }

    @Override
    public void setCreationDate(Date creationDate) {

    }
}
