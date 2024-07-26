package org.ICIQ.eChempad.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldControllerImpl {
    @GetMapping("/health")
    public String helloWorld() {
        return "UP";
    }
}
