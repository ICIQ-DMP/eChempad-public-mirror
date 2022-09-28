package org.ICIQ.eChempad.configurations.wrappers;

import org.ICIQ.eChempad.entities.genericJPAEntities.Researcher;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserDetailsImpl implements UserDetails{

    private final Researcher researcher;

    public UserDetailsImpl(Researcher researcher) {
        this.researcher = researcher;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return (Collection<? extends GrantedAuthority>) this.researcher.getAuthorities();
    }

    @Override
    public String getPassword() {
        return this.researcher.getPassword();
    }

    @Override
    public String getUsername() {
        return this.researcher.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.researcher.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.researcher.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.researcher.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.researcher.isEnabled();
    }
}
