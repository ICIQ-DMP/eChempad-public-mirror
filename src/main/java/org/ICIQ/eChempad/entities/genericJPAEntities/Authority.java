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
package org.ICIQ.eChempad.entities.genericJPAEntities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@javax.persistence.Entity
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "typeName",
        defaultImpl = Authority.class)
public class Authority extends EntityImpl implements GrantedAuthority{

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

    public Authority() {}

    public Authority(String authority, Researcher researcher) {
        this.authority = authority;
        this.researcher = researcher;
        this.initCreationDate();
    }

    /**
     * Implemented by every class to return its own type.
     *
     * @return Class of the object implementing this interface.
     */
    @Override
    public <T extends Entity> Class<T> getType() {
        return (Class<T>) Authority.class;
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
