/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * https://wstutorial.com/rest/spring-boot-security-custom-authentication.html
 *
 * This class overrides the default authentication in Spring security in order to increase the granularity of the
 * permissions against resources to forbid researchers to access other researchers resources. It will be also used to
 * provide a secure administrative account.
 */
public class CustomFineGrainedAuthenticationProvider //implements AuthenticationProvider
{

    /**
     * Receives an authentication object. This object contains the data from a user that has been authenticated
     * correctly. As such, an authentication object shares data with a UserDetails, which in this app the UserDetails
     * interface is implemented by the Researcher entity class. At this point of the security chain, the Authentication
     * only contains the user identified by a unique string in the DB (in our case an email) and a password, that can
     * be incorrect.
     *
     * What we need to do is create a new authentication and return it upstream. This authentication object will contain
     * the same data of the user that is trying to authenticate plus its granted authorities. We need to check if the
     * credentials are valid to authenticate; if not we return an exception.
     * @param authentication Contains unchecked credentials of a user that want s to authenticate against the app.
     * @return An authentication object with the same data as the received, but with validated credentials and granted
     * authorities.
     * @throws AuthenticationException If the user can not be authenticated then an exception is raised.
     */
    /*
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return null;
    }
    */



    /**
     * Receives a class representing a type of authentication and returns a boolean to indicate if the authenticate
     * method is apt to this authentication method.
     * @param aClass Class representing an authorization method, such as HttpBasic (?)
     * @return Returns true if the provided authentication method in the parameter it can be used; false otherwise.
     */
    /*@Override
    public boolean supports(Class<?> aClass) {
        return false;
    }
    */



}
