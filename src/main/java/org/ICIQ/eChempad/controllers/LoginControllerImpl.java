package org.ICIQ.eChempad.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginControllerImpl implements LoginController {

    @GetMapping("/login")
    @Override
    public String login() {
        return "login";
    }
}
