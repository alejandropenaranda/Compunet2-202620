package com.compunet.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.compunet.springboot.model.Usuario;

import org.springframework.ui.Model;


@Controller 
public class TemplateController {
    
    @GetMapping("/saludo")
    public String saludo(Model model) {
        model.addAttribute("title", "¡Hola, mundo!");
        Usuario user = new Usuario();
        user.setNombre("Alejandro");
        user.setApellido("Penaranda");
        user.setCorreoInstitucional("alejopenarnada@icesi.edu.co");
        user.setPassword("secreto123");
        user.setActive(true);
        model.addAttribute("usuario", user);
        return "saludo";
    }
    
}
