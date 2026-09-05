package com.compunet.springboot.controller;

import org.springframework.web.bind.annotation.RestController;

import com.compunet.springboot.model.Curso;
import com.compunet.springboot.model.Profesor;
import com.compunet.springboot.repository.CursoRepository;
import com.compunet.springboot.repository.ProfesorRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class Controller {

    private CursoRepository cursoRepository;
    private ProfesorRepository profesorRepository;

    @Autowired
    public Controller (CursoRepository cursoRepo, ProfesorRepository profesorRepo){
        this.cursoRepository = cursoRepo;
        this.profesorRepository = profesorRepo;
    }
    
    @GetMapping("/")
    public String home() {
        return "!Proyecto Spring boot funcionando correctamente¡";
    }


    @GetMapping("/cursos")
    public List<Curso> getCursos() {
        // Aquí ocurre la magia: 
        // 1. Spring Data JPA llama a Hibernate.
        // 2. Hibernate ejecuta: SELECT * FROM cursos.
        // 3. Hibernate convierte las filas a objetos Curso.
        // 4. Spring convierte (serializa) la lista de objetos Java a formato JSON para el navegador.
        return cursoRepository.findAll();
    }
    

    @GetMapping("/profesores")
    public List<Profesor> getProfesors (){
        
        return  profesorRepository.findAll();
    }
    
    
}
