package org.ICIQ.eChempad.configurations.Security;

import org.ICIQ.eChempad.configurations.Helpers.UserDetailsImpl;
import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
