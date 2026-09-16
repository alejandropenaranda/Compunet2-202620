package com.compunet.springboot.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compunet.springboot.model.Curso;
import com.compunet.springboot.repository.CursoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;

    @Transactional(readOnly = true)
    public List<Curso> listarTodos() {
        return cursoRepository.findAll();
    }

    /**
     * Ejercicio 4: Obtener cursos dentro de un rango de créditos.
     */
    @Transactional(readOnly = true)
    public List<Curso> listarPorRangoCreditos(int min, int max) {
        return cursoRepository.findByCreditosBetween(min, max);
    }

    /**
     * Ejercicio 5: Buscar cursos por coincidencia en el nombre (ignore case).
     */
    @Transactional(readOnly = true)
    public List<Curso> buscarCursosPorNombre(String texto) {
        return cursoRepository.findByNombreContainingIgnoreCase(texto);
    }

    /**
     * Ejercicio 9: Obtener cursos asignados a un profesor a partir de su ID.
     */
    @Transactional(readOnly = true)
    public List<Curso> listarCursosDeProfesor(Long profesorId) {
        return cursoRepository.findByProfesor_Id(profesorId);
    }

    /**
     * Ejercicio 10: Obtener cursos según el departamento del profesor.
     */
    @Transactional(readOnly = true)
    public List<Curso> listarCursosPorDepartamentoDelProfesor(String depto) {
        return cursoRepository.findByProfesor_DepartamentoIgnoreCase(depto);
    }

    /**
     * Ejercicio 13: Obtener cursos con créditos mayores o iguales a un mínimo, ordenados de mayor a menor.
     */
    @Transactional(readOnly = true)
    public List<Curso> obtenerCursosPorCreditosMinimos(int creditosMin) {
        return cursoRepository.findByCreditosGreaterThanEqualOrderByCreditosDesc(creditosMin);
    }
}
