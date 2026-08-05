package com.example.repository;

import com.example.model.Estudiante;
import java.util.ArrayList;
import java.util.List;

public class EstudianteRepositoryInMemory implements EstudianteRepository {

    private final List<Estudiante> estudiantes = new ArrayList<>();

    public EstudianteRepositoryInMemory() {
        
    }

    // Método invocado al inicializar el Bean
    public void iniciarRepositorio() {
        System.out.println("-> [LIFECYCLE] Inicializando repositortio y cargando datos iniciales...");

        estudiantes.add(new Estudiante("1", "Ana Gómez", "ana@icesi.edu.co"));
        estudiantes.add(new Estudiante("2", "Carlos Pérez", "carlos@icesi.edu.co"));
        estudiantes.add(new Estudiante("3", "María López", "maria@icesi.edu.co"));
    }

    // Método invocado al destruir el Bean
    public void limpiarRecursos() {
        System.out.println("-> [LIFECYCLE] Eliminando datos y iberando memoria...");
    }

    @Override
    public List<Estudiante> obtenerTodos() {
        return new ArrayList<>(this.estudiantes);
    }

    @Override
    public void guardar(Estudiante estudiante) {
        this.estudiantes.add(estudiante);
    }
}