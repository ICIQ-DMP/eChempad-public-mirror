package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.entities.Researcher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

/**
 * Non-generic functions used to manipulate the in-memory data structures of the researchers. The generic calls are
 * provided in GenericServiceClass
 */
public interface ResearcherService extends GenericService<Researcher, UUID>, UserDetailsService {


    /**
     * Generates a JWT token that is used by the researcher to authenticate his requests. This token has a time
     * expiration, contains the granted authorities associated with the token (things we can do with this token) and is
     * signed using the researcher password
     * @param researcher Instance of the researcher that will be used to generate the token.
     * @return JWT token, containing granted authorities, expiration date and digital signature.
     */
    String generateJWTToken(Researcher researcher);


    /**
     * Gets the UserDetails instance of an user if required by using the user repository underneath by using its email.
     * Used to obtain credentials and permissions and perform authentication and authorization.
     * @param email email of the researcher that we are retrieving data from. Unique in the database.
     * @return Returns all the information of an user using and UserDetails class, which is used by spring to manage the
     * user authentication internally.
     */
    UserDetails loadDetailsByUsername(String email);
}
