package com.compunet.springboot.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compunet.springboot.model.Estudiante;
import com.compunet.springboot.repository.EstudianteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;

    @Transactional(readOnly = true)
    public List<Estudiante> listarTodos() {
        return estudianteRepository.findAll();
    }

    /**
     * Ejercicio 7: Obtener estudiantes cuyo correo termine con un dominio dado.
     */
    @Transactional(readOnly = true)
    public List<Estudiante> filtrarPorDominio(String dominio) {
        return estudianteRepository.findByCorreoInstitucionalEndingWithIgnoreCase(dominio);
    }

    /**
     * Ejercicio 8: Contar el total de estudiantes activos.
     */
    @Transactional(readOnly = true)
    public long contarEstudiantesActivos() {
        return estudianteRepository.countByActiveTrue();
    }

    /**
     * Ejercicio 15: Listar estudiantes activos de un curso ordenados por apellido (JPQL).
     */
    @Transactional(readOnly = true)
    public List<Estudiante> listarEstudiantesDeCursoJPQL(Long cursoId) {
        return estudianteRepository.buscarEstudiantesPorCursoJPQL(cursoId);
    }

    /**
     * Ejercicio 15: Listar estudiantes activos de un curso ordenados por apellido (Native Query).
     */
    @Transactional(readOnly = true)
    public List<Estudiante> listarEstudiantesDeCursoNativo(Long cursoId) {
        return estudianteRepository.buscarEstudiantesPorCursoNativo(cursoId);
    }
}
