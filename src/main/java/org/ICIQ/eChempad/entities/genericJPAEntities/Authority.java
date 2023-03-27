/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.entities.genericJPAEntities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.ICIQ.eChempad.configurations.converters.UUIDConverter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;


@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "typeName",
        defaultImpl = Authority.class)
public class Authority extends JPAEntityImpl implements GrantedAuthority{
    @Id
    @Convert(converter = UUIDConverter.class)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", nullable = false, unique = true)
    protected UUID id;

    // Authority that this user has against this resource.
    @Column(name = "authority", length = 100, nullable = false)
    protected String authority;

    @JoinColumn(name = "researcher", nullable = true)
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JsonBackReference
    protected Researcher researcher;

    /**
     * Date of creation of the entity.
     */
    private Date creationDate;

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Authority() {}

    public Authority(String authority, Researcher researcher) {
        this.authority = authority;
        this.researcher = researcher;
        this.initCreationDate();
    }

    public Authority(String authority) {
        this.authority = authority;
        this.initCreationDate();
    }

    @Override
    public Serializable getId() {
        return this.id;
    }

    @Override
    public void setId(Serializable id) {
        this.id = (UUID)id;
    }

    /**
     * Implemented by every class to return its own type.
     *
     * @return Class of the object implementing this interface.
     */
    @Override
    public <T extends JPAEntity> Class<T> getType() {
        return (Class<T>) Authority.class;
    }

    /**
     * Obtains the typeName, used by jackson to deserialize generics.
     *
     * @return Name of the class as string.
     */
    @Override
    public String getTypeName() {
        return this.getType().getName();
    }

    @Override
    public void initCreationDate() {
        this.creationDate = new Date();
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
    public String getAuthority() {
        return this.authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public Researcher getResearcher() {
        return researcher;
    }

    public void setResearcher(Researcher researcher) {
        this.researcher = researcher;
    }

    @Override
    public String toString() {
        return "Authority{" +
                "authority='" + authority + '\'' +
                '}';
    }
}
