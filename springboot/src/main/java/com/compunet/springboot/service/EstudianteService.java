package com.compunet.springboot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.compunet.springboot.model.Estudiante;
import com.compunet.springboot.repository.EstudianteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;

    public List<Estudiante> listarTodos() {
        return estudianteRepository.findAll();
    }

    /**
     * Ejercicio 7: Obtener estudiantes cuyo correo termine con un dominio dado.
     */
    public List<Estudiante> filtrarPorDominio(String dominio) {
        return estudianteRepository.findByCorreoInstitucionalEndingWithIgnoreCase(dominio);
    }

    /**
     * Ejercicio 8: Contar el total de estudiantes activos.
     */
    public long contarEstudiantesActivos() {
        return estudianteRepository.countByActiveTrue();
    }

    /**
     * Ejercicio 15: Listar estudiantes activos de un curso ordenados por apellido (JPQL).
     */
    public List<Estudiante> listarEstudiantesDeCursoJPQL(Long cursoId) {
        return estudianteRepository.buscarEstudiantesPorCursoJPQL(cursoId);
    }

    /**
     * Ejercicio 15: Listar estudiantes activos de un curso ordenados por apellido (Native Query).
     */
    public List<Estudiante> listarEstudiantesDeCursoNativo(Long cursoId) {
        return estudianteRepository.buscarEstudiantesPorCursoNativo(cursoId);
    }
}
