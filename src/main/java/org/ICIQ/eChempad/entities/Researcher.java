/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.entities;

import org.ICIQ.eChempad.configurations.Converters.UUIDConverter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;


/**
 * Used to store information about the User and its workspace.
 *
 * It has a list containing the different Journal that conform the workspace.
 */
@Entity
@Table(
        uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id", "username"})
})
public class Researcher implements Serializable, IEntity, UserDetails {
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Convert(converter = UUIDConverter.class)
    @Column(unique = true)
    private UUID id;

    //TODO set a certain length for the used hashed algorithm
    @Column(length = 50, nullable = false)
    private String password;

    @Id
    @Column(length = 50, nullable = false, unique = true)
    private String username;

    @NotNull
    private boolean accountNonExpired;

    @NotNull
    private boolean accountNonLocked;

    @NotNull
    private boolean credentialsNonExpired;

    @NotNull
    private boolean enabled;

    // Exactly 73 characters
    @Column(length = 73, nullable = true)
    private String signalsAPIKey;

    @OneToMany(
            targetEntity = Authority.class, orphanRemoval = true, mappedBy = "authority"
    )
    Set<GrantedAuthority> authorities = new HashSet<>();


    public Researcher() {}


    // GETTERS AND SETTERS

    public Serializable getId() {
        return this.id;
    }

    public void setId(Serializable s) {
        this.id = (UUID) s;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSignalsAPIKey() {
        return this.signalsAPIKey;
    }

    public void setSignalsAPIKey(String signalsAPIKey) {
        this.signalsAPIKey = signalsAPIKey;
    }


    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return the password
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Returns the username used to authenticate the user. Cannot return
     * <code>null</code>.
     *
     * @return the username (never <code>null</code>)
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * Indicates whether the user's account has expired. An expired account cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user's account is valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
