package com.example.service;

import com.example.model.Estudiante;
import com.example.repository.EstudianteRepository;
import java.util.List;

public class EstudianteServiceImpl implements EstudianteService {

    private final EstudianteRepository estudianteRepository;

    // Dependencia requerida inyectada por constructor
    public EstudianteServiceImpl(EstudianteRepository estudianteRepository) {
        this.estudianteRepository = estudianteRepository;
    }

    // Forma de realizar la inyección de dependencias por setter

    // public EstudianteServiceSetterImpl() {
    // }

    // // Setter para inyección de dependencia
    // public void setEstudianteRepository(EstudianteRepository estudianteRepository) {
    //     this.estudianteRepository = estudianteRepository;
    // }

    @Override
    public List<Estudiante> listarEstudiantes() {
        return this.estudianteRepository.obtenerTodos();
    }

    @Override
    public void registrarEstudiante(Estudiante estudiante) {
        if (estudiante.getCorreo() == null || !estudiante.getCorreo().contains("@")) {
            throw new IllegalArgumentException("El correo electrónico no es válido");
        }
        this.estudianteRepository.guardar(estudiante);
    }
}