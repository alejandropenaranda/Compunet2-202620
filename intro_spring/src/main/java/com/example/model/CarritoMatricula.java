package com.example.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CarritoMatricula {
    private Estudiante estudiante;
    private List<String> cursos;

    public CarritoMatricula() {
        this.cursos = new ArrayList<>();
        System.out.println("--- [SPRING] Instanciando un nuevo CarritoMatricula (Hash: " + System.identityHashCode(this) + ") ---");
    }

    public void asignarEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public void agregarCurso(String curso) {
        this.cursos.add(curso);
    }

    public List<String> getCursos() {
        return cursos;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }
};  

