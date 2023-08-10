/*
 * eChempad is a suite of web services oriented to manage the entire
 * data life-cycle of experiments and assays from Experimental
 * Chemistry and related Science disciplines.
 *
 * Copyright (C) 2021 - present Institut Català d'Investigació Química (ICIQ)
 *
 * eChempad is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.ICIQ.eChempad.entities.genericJPAEntities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


/**
 * Used to store information about the researcher that is using the application. It includes all relevant data
 * associated with the logged user, such as password, email, keys associated with its account, etc.
 * <p>
 * This class is a database entity, but it is also used as mould for serializing and deserializing.
 */
@jakarta.persistence.Entity
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "typeName",
        defaultImpl = Researcher.class)
public class Researcher extends EntityImpl implements Entity{

    //TODO set a certain length for the used hashed algorithm
    @Column(length = 50, nullable = false)
    private String password;

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

    // Exactly 37 characters
    @Column(length = 37, nullable = true)
    private String dataverseAPIKey;

    @OneToMany(
            targetEntity = Authority.class,
            mappedBy = "researcher",
            cascade = {CascadeType.ALL},
            fetch = FetchType.EAGER,  // Eager so the authorizations are loaded when loading a researcher
            // if a researcher is deleted all of its Permissions have to be deleted.
            orphanRemoval = true  // cascade = CascadeType.ALL  https://stackoverflow.com/questions/16898085/jpa-hibernate-remove-entity-sometimes-not-working
    )
    @JsonManagedReference
    private Set<Authority> authorities = new HashSet<>();

    public Researcher() {}

    // For generic deserialization for the authority, since needs a constructor for Researcher using String in the
    // deserialization
    public Researcher(String uuid)
    {
        this.id = UUID.fromString(uuid);
    }

    public Researcher(UUID id, String password, String username, boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled, String signalsAPIKey) {
        this.id = id;
        this.password = password;
        this.username = username;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
        this.signalsAPIKey = signalsAPIKey;
    }

    @Override
    public String toString() {
        return "Researcher{" +
                "password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", accountNonExpired=" + accountNonExpired +
                ", accountNonLocked=" + accountNonLocked +
                ", credentialsNonExpired=" + credentialsNonExpired +
                ", enabled=" + enabled +
                ", signalsAPIKey='" + signalsAPIKey + '\'' +
                ", dataverseAPIKey='" + dataverseAPIKey + '\'' +
                ", authorities=" + authorities +
                ", id=" + id +
                '}';
    }

// GETTERS AND SETTERS

    public Serializable getId() {
        return this.id;
    }

    public void setId(Serializable s) {
        this.id = (UUID) s;
    }

    /**
     * Implemented by every class to return its own type.
     *
     * @return Class of the object implementing this interface.
     */
    @Override
    public <T extends Entity> Class<T> getType() {
        return (Class<T>) Researcher.class;
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


    // GETTERS AND SETTERS

    /**
     * Returns the password used to authenticate the user.
     *
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * Returns the username used to authenticate the user. Cannot return
     * <code>null</code>.
     *
     * @return the username (never <code>null</code>)
     */
    public String getUsername() {
        return this.username;
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
     * Indicates whether the user's account has expired. An expired account cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user's account is valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
     */
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }


    /**
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }


    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> permissions) {
        this.authorities = permissions;
    }

    public String getDataverseAPIKey() {
        return dataverseAPIKey;
    }

    public void setDataverseAPIKey(String dataverseAPIKey) {
        this.dataverseAPIKey = dataverseAPIKey;
    }

}
