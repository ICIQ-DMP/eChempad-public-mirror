package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.entities.Researcher;

import java.util.UUID;

/**
 * Non-generic functions used to manipulate the in-memory data structures of the researchers. The generic calls are
 * provided in GenericServiceClass
 */
public interface ResearcherService extends GenericService<Researcher, UUID> {

    /**
     * Generates a JWT token that is used by the researcher to authenticate his requests. This token has a time
     * expiration, contains the granted authorities associated with the token (things we can do with this token) and is
     * signed using the researcher password
     * @param researcher Instance of the researcher that will be used to generate the token.
     * @return JWT token, containing granted authorities, expiration date and digital signature.
     */
    String generateJWTToken(Researcher researcher);
}
