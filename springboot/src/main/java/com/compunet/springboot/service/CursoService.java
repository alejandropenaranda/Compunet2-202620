package com.compunet.springboot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.compunet.springboot.model.Curso;
import com.compunet.springboot.model.Profesor;
import com.compunet.springboot.repository.CursoRepository;
import com.compunet.springboot.repository.ProfesorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;
    private final ProfesorRepository profesorRepository;

    /**
     * Obtener todos los cursos registrados.
     */
    public List<Curso> listarTodos() {
        return cursoRepository.findAll();
    }

    /**
     * Obtener un curso por su ID.
     */
    public Optional<Curso> obtenerPorId(Long id) {
        return cursoRepository.findById(id);
    }

    /**
     * Registrar un nuevo curso asignándole su profesor responsable.
     */
    public Curso registrarCurso(Curso curso, Long profesorId) {
        Profesor profesor = profesorRepository.findById(profesorId)
                .orElseThrow(() -> new IllegalArgumentException("El profesor con ID " + profesorId + " no existe."));
        curso.setProfesor(profesor);
        return cursoRepository.save(curso);
    }

    /**
     * Actualizar los datos de un curso existente y/o su profesor asignado.
     */
    public Curso actualizarCurso(Long id, Curso cursoActualizado, Long profesorId) {
        Curso cursoDb = cursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el curso con ID: " + id));

        cursoDb.setNombre(cursoActualizado.getNombre());
        cursoDb.setCreditos(cursoActualizado.getCreditos());
        cursoDb.setDepartamento(cursoActualizado.getDepartamento());

        if (profesorId != null) {
            Profesor profesor = profesorRepository.findById(profesorId)
                    .orElseThrow(() -> new IllegalArgumentException("El profesor con ID " + profesorId + " no existe."));
            cursoDb.setProfesor(profesor);
        }

        return cursoRepository.save(cursoDb);
    }

    /**
     * Eliminar físicamente un curso por ID.
     */
    public void eliminarCurso(Long id) {
        if (!cursoRepository.existsById(id)) {
            throw new IllegalArgumentException("No se encontró el curso con ID: " + id);
        }
        cursoRepository.deleteById(id);
    }

    // ==========================================
    // MÉTODOS DE CONSULTA Y EJERCICIOS ACADÉMICOS
    // ==========================================

    /**
     * Ejercicio 4: Obtener cursos dentro de un rango de créditos.
     */
    public List<Curso> listarPorRangoCreditos(int min, int max) {
        return cursoRepository.findByCreditosBetween(min, max);
    }

    /**
     * Ejercicio 5: Buscar cursos por coincidencia en el nombre (ignore case).
     */
    public List<Curso> buscarCursosPorNombre(String texto) {
        return cursoRepository.findByNombreContainingIgnoreCase(texto);
    }

    /**
     * Ejercicio 9: Obtener cursos asignados a un profesor a partir de su ID.
     */
    public List<Curso> listarCursosDeProfesor(Long profesorId) {
        return cursoRepository.findByProfesor_Id(profesorId);
    }

    /**
     * Ejercicio 10: Obtener cursos según el departamento del profesor.
     */
    public List<Curso> listarCursosPorDepartamentoDelProfesor(String depto) {
        return cursoRepository.findByProfesor_DepartamentoIgnoreCase(depto);
    }

    /**
     * Ejercicio 13: Obtener cursos con créditos mayores o iguales a un mínimo, ordenados de mayor a menor.
     */
    public List<Curso> obtenerCursosPorCreditosMinimos(int creditosMin) {
        return cursoRepository.findByCreditosGreaterThanEqualOrderByCreditosDesc(creditosMin);
    }
}
