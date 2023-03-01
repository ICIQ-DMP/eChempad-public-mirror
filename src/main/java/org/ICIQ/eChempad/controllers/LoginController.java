package org.ICIQ.eChempad.controllers;

/**
 * Defines the operations that a login controller should have.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 1/3/2022
 */
public interface LoginController {

    /**
     * Returns the needed information to access the login form.
     *
     * @return the {@code String} that needs to be put after the base URL to access the login form
     */
    String login();
}
