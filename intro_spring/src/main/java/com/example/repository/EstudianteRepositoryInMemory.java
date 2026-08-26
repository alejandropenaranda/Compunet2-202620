package com.example.repository;

import com.example.model.Estudiante;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class EstudianteRepositoryInMemory implements EstudianteRepository {

    private final List<Estudiante> estudiantes = new ArrayList<>();

    public EstudianteRepositoryInMemory() {
        
    }
    // Método invocado al inicializar el Bean
    @PostConstruct
    public void iniciarRepositorio() {
        System.out.println("-> [LIFECYCLE] @PostConstruct: Inicializando repositortio y cargando datos iniciales...");

        estudiantes.add(new Estudiante("1", "Ana Gómez", "ana@icesi.edu.co"));
        estudiantes.add(new Estudiante("2", "Carlos Pérez", "carlos@icesi.edu.co"));
        estudiantes.add(new Estudiante("3", "María López", "maria@icesi.edu.co"));
    }

    // Método invocado al destruir el Bean
    @PreDestroy
    public void limpiarRecursos() {
        System.out.println("-> [LIFECYCLE] @PreDestoy Eliminando datos y iberando recursos...");
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