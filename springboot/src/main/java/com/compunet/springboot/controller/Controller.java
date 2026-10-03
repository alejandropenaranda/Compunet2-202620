package com.compunet.springboot.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.compunet.springboot.model.Curso;
import com.compunet.springboot.model.Profesor;
import com.compunet.springboot.model.Usuario;
import com.compunet.springboot.repository.CursoRepository;
import com.compunet.springboot.repository.ProfesorRepository;
import com.compunet.springboot.service.UsuarioService;

@RestController
public class Controller {

    private final UsuarioService usuarioService;
    private final CursoRepository cursoRepository;
    private final ProfesorRepository profesorRepository;

    public Controller(CursoRepository cursoRepo,
            ProfesorRepository profesorRepo,
            UsuarioService usuarioService) {
        this.cursoRepository = cursoRepo;
        this.profesorRepository = profesorRepo;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String home() {
        return "!Proyecto Spring boot funcionando correctamente¡";
    }

    @GetMapping("/cursos")
    public List<Curso> getCursos() {
        return cursoRepository.findAll();
    }

    @GetMapping("/profesores")
    public List<Profesor> getProfesors() {
        return profesorRepository.findAll();
    }

    @GetMapping("/usuario")
    public List<Usuario> getUsuario() {
        return usuarioService.listarUsuariosActivos();
    }

    @GetMapping("/crear-usuario")
    public Usuario crearUsuario() {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre("Alejandro");
        nuevoUsuario.setApellido("Peñaranda");
        nuevoUsuario.setCorreoInstitucional("apenaranda@icesi.edu.co");
        nuevoUsuario.setPassword("password");

        return usuarioService.registrarUsuario(nuevoUsuario, "ESTUDIANTE");
    }
}

