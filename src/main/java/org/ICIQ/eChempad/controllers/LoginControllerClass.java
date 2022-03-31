/**
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.services.JournalServiceClass;
import org.ICIQ.eChempad.services.ResearcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LoginControllerClass implements LoginController {

    private final ResearcherService researcherService;

    @Autowired
    public LoginControllerClass(ResearcherService researcherService) {
        this.researcherService = researcherService;
    }

    // https://blog.softtek.com/es/autenticando-apis-con-spring-y-jwt
    @PostMapping
    public String login(@RequestParam("email") String email, @RequestParam("password") String password) {
        // First validate that the hash of the received password is the same as the hash we have stored for the desired user
        // If not throw authentication exception
        // If is the same, generate the token and return it to the user
        //String token = getJWTToken(researcherName);

        //Researcher researcher = new Researcher();
        //researcher.setFullName(researcherName);
        return null;
    }

}
