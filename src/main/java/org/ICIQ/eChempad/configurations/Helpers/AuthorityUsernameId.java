package org.ICIQ.eChempad.configurations.Helpers;

import java.io.Serializable;
import java.util.Objects;

public class AuthorityUsernameId implements Serializable {
    private String authority;
    private String username;

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorityUsernameId that = (AuthorityUsernameId) o;
        return getAuthority().equals(that.getAuthority()) && getUsername().equals(that.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAuthority(), getUsername());
    }
}
