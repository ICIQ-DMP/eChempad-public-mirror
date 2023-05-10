/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2023 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.controllers.UIControllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIControllerImpl implements UIController {

    @GetMapping("/login")
    @Override
    public String login() {
        return "login";
    }

    @GetMapping("/exit")
    @Override
    public String exit() {
        return "exit";
    }

    @GetMapping("/timeout")
    @Override
    public String timeout() {
        return "timeout";
    }

    @GetMapping("/main")
    @Override
    public String main() {
        return "main";
    }

    @GetMapping("/help")
    @Override
    public String help() {
        return "help";
    }

    @GetMapping("/profile")
    @Override
    public String profile() {
        return "profile";
    }
}
