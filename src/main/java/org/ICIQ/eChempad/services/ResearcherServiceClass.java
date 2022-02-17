package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

//import io.jsonwebtoken.Jwts;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResearcherServiceClass extends GenericServiceClass<Researcher, UUID> implements ResearcherService{

    //private ResearcherRepository researcherRepository;

    @Autowired
    public ResearcherServiceClass(ResearcherRepository researcherRepository) {
        super(researcherRepository);
    }


    @Override
    public Researcher saveOrUpdate(Researcher researcher) {

        return this.genericRepository.saveOrUpdate(researcher);
    }

    @Override
    /**
     * Generates a JWT token that is used by the researcher to authenticate his requests. This token has a time
     * expiration, contains the granted authorities associated with the token (things we can do with this token) and is
     * signed using the researcher password
     * @param researcher Instance of the researcher that will be used to generate the token.
     * @return JWT token, containing granted authorities, expiration date and digital signature.
     */
    public String generateJWTToken(Researcher researcher)
    {
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
}
