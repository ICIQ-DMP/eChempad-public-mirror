package org.ICIQ.eChempad.configurations.Security;

import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

@Service
public class UserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private ResearcherRepository<Researcher, UUID> researcherRepository;

    @Autowired
    private AclService aclService;

    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Researcher researcher = this.researcherRepository.findByUsername(username);
        if (researcher == null) {
            throw new UsernameNotFoundException(username);
        }

        return new UserDetailsImpl(researcher);
    }

}
