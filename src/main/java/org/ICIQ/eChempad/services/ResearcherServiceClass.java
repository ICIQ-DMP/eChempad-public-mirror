package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.ElementPermission;
import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.entities.Role;
import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

//import io.jsonwebtoken.Jwts;

import java.security.Permissions;
import java.util.*;

@Service
public class ResearcherServiceClass extends GenericServiceClass<Researcher, UUID> implements ResearcherService {

    @Autowired
    public ResearcherServiceClass(ResearcherRepository researcherRepository) {
        super(researcherRepository);
    }


    @Override
    public Researcher saveOrUpdate(Researcher researcher) {

        return this.genericRepository.saveOrUpdate(researcher);
    }

    /**
     * Generates a JWT token that is used by the researcher to authenticate his requests. This token has a time
     * expiration, contains the granted authorities associated with the token (things we can do with this token) and is
     * signed using the researcher password
     *
     * @param researcher Instance of the researcher that will be used to generate the token.
     * @return JWT token, containing granted authorities, expiration date and digital signature.
     */
    @Override
    public String generateJWTToken(Researcher researcher) {
        String secretKey = "myArbitrarySecretKey";

        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");
/*
        String token =
                Jwts
                .builder()
                .setId("softtekJWT")
                .setSubject(researcher.getEmail())
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(SignatureAlgorithm.HS512,
                        secretKey.getBytes()).compact();

        return "Bearer " + token;
*/
        return null;
    }


    /**
     * Exposed (? //RF make public and the inner private if it does conform) method to obtain the details of the user in
     * the web application in order to authenticate it.
     * @param email email of the researcher that we are retrieving data from. Unique in the database.
     * @return Returns an instance of UserDetails, which contains all the data necessary to manage an authentication and
     * authorization of an user.
     */
    @Override
    public UserDetails loadDetailsByUsername(String email) {
        return this.loadUserByUsername(email);
    }


    @Override
    public Map<UUID, UserDetails> loadAllUserDetails() {
        Map<UUID, UserDetails> ret = new HashMap<>();

        for (Researcher res: this.genericRepository.getAll())
        {
            ret.put(res.getUUid(), this.loadUserByUsername(res.getEmail()));
        }
        return ret;
    }

    /**
     * Internal method that will be used to authenticate users. Basically it transforms a Researcher entity to a
     * UserDetails entity identified by its email. To construct a user details we need to load its username, roles and
     * password.
     *
     * @param s email of the researcher that we are retrieving data from. Unique in the database.
     * @return A UserDetails instance corresponding to the email received by parameter.
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<Researcher> selected = ((ResearcherRepository) this.genericRepository).getResearcherByEmail(s);

        if (selected.isPresent()) {
            Researcher researcher = selected.get();

            // Obtain the roles of this user to construct the instance of UserDetails for SpringBoot Security
            Map<UUID, ElementPermission> permissions = researcher.getPermissions();

            Set<String> roles = new HashSet<>();

            // All users have the USER roll, which is the most basic permissions that allows the access to public
            // resources
            roles.add("USER");


            // We append as a role the UUID of the resource that we want to access, and _ + permission against this resource
            if (!CollectionUtils.isEmpty(permissions)) {
                for (UUID id : permissions.keySet()) {
                    // Construct an string representing resourceID_ + role
                    roles.add(permissions.get(id).getResource() + "_" + permissions.get(id).getRole().name());
                }
            }

            return org.springframework.security.core.userdetails.User
                    .withUsername(s)
                    .roles(roles.toArray(new String[0]))
                    .password(researcher.getHashedPassword())
                    .build();
        } else {
            throw new UsernameNotFoundException("The researcher with email " + s + " is not registered in the database");
        }
    }



}


