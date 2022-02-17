package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Researcher;

public interface LoginController {

    /**
     * Log in the researcher into the API REST by returning a JWT token that the researcher can be used to authenticate
     * on each request by putting it in the HTTP headers.
     * @param email Email of the researcher. E-mail must be unique for each researcher.
     * @param password Password of the researcher, which has to be hashed and compared with the stored password in the
     *                 database.
     * @return JWT token used by the researcher to authenticate his requests.
     */
    String login(String email, String password);

}
