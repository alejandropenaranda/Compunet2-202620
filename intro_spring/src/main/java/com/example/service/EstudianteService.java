package com.example.service;

import java.util.List;

import com.example.model.Estudiante;

public interface EstudianteService {
    List<Estudiante> listarEstudiantes();
    void registrarEstudiante(Estudiante estudiante);
    
}
