package com.compunet.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller 
public class LoginController {
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping({"/access-denied", "/acces-denied"})
    public String accessDenied() {
        return "acces-denied"; // Renderiza templates/acces-denied.html
    }
    
}
