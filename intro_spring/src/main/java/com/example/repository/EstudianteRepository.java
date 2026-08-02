package com.example.repository;

import java.util.List;

import com.example.model.Estudiante;

public interface EstudianteRepository {

    List<Estudiante> obtenerTodos();
    void guardar(Estudiante estudiante);

}
