/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name="role_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "UUID")
})
public class RoleUser implements IEntity{
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "UUID")
    private UUID id;


    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JoinColumn(
            name = "researcher_id",
            nullable = false)
    @JsonIgnore
    private Researcher researcher;


    // Authority that this user has against this resource.
    @Column(name = "role", length = 100, nullable = false)
    private Role role;


    public RoleUser() {}

    public RoleUser(Researcher researcher, Role role) {
        this.researcher = researcher;
        this.role = role;
    }

    public Researcher getResearcher() {
        return researcher;
    }

    public void setResearcher(Researcher researcher) {
        this.researcher = researcher;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public UUID getUUid() {
        return null;
    }

    @Override
    public void setUUid(UUID id) {

    }

    @Override
    public <T extends IEntity> Class<T> getMyType() {
        return (Class<T>) RoleUser.class;
    }

    @Override
    public boolean isContainer(UUID entity_uuid) {
        return false;
    }

    @Override
    public boolean isContained(UUID entity_uuid) {
        return false;
    }

    @Override
    public String toString() {
        return "RoleUser{" +
                "researcher=" + researcher +
                ", role=" + role +
                '}';
    }
}
