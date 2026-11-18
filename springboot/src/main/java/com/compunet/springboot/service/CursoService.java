package com.compunet.springboot.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compunet.springboot.model.Curso;
import com.compunet.springboot.model.Profesor;
import com.compunet.springboot.repository.CursoRepository;
import com.compunet.springboot.repository.MatriculaRepository;
import com.compunet.springboot.repository.ProfesorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;
    private final ProfesorRepository profesorRepository;
    private final MatriculaRepository matriculaRepository;

    /**
     * Obtener todos los cursos registrados.
     */
    @Transactional(readOnly = true)
    public List<Curso> listarTodos() {
        return cursoRepository.findAll();
    }

    /**
     * Obtener un curso por su ID.
     */
    @Transactional(readOnly = true)
    public Optional<Curso> obtenerPorId(Long id) {
        return cursoRepository.findById(id);
    }

    /**
     * Consultar cursos filtrando por departamento (insensible a mayúsculas/minúsculas).
     */
    @Transactional(readOnly = true)
    public List<Curso> listarPorDepartamento(String departamento) {
        if (departamento == null || departamento.isBlank()) {
            return cursoRepository.findAll();
        }
        return cursoRepository.findByDepartamentoIgnoreCase(departamento.trim());
    }

    /**
     * Consultar cursos asignados a un docente a partir del ID de Profesor.
     */
    @Transactional(readOnly = true)
    public List<Curso> listarPorProfesorId(Long profesorId) {
        if (profesorId == null || !profesorRepository.existsById(profesorId)) {
            throw new NoSuchElementException("No se encontró el profesor con ID: " + profesorId);
        }
        return cursoRepository.findByProfesorId(profesorId);
    }

    /**
     * Registrar un nuevo curso asignándole su profesor responsable.
     */
    @Transactional
    public Curso registrarCurso(Curso curso, Long profesorId) {
        if (profesorId == null) {
            throw new IllegalArgumentException("El ID del profesor es obligatorio.");
        }
        Profesor profesor = profesorRepository.findById(profesorId)
                .orElseThrow(() -> new NoSuchElementException("El profesor con ID " + profesorId + " no existe."));
        curso.setProfesor(profesor);
        return cursoRepository.save(curso);
    }

    /**
     * Actualizar los datos de un curso existente y/o su profesor asignado.
     */
    @Transactional
    public Curso actualizarCurso(Long id, Curso cursoActualizado, Long profesorId) {
        Curso cursoDb = cursoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el curso con ID: " + id));

        cursoDb.setNombre(cursoActualizado.getNombre());
        cursoDb.setCreditos(cursoActualizado.getCreditos());
        cursoDb.setDepartamento(cursoActualizado.getDepartamento());

        if (profesorId != null) {
            Profesor profesor = profesorRepository.findById(profesorId)
                    .orElseThrow(() -> new NoSuchElementException("El profesor con ID " + profesorId + " no existe."));
            cursoDb.setProfesor(profesor);
        }

        return cursoRepository.save(cursoDb);
    }

    /**
     * Eliminar físicamente un curso por ID validando integridad referencial.
     */
    @Transactional
    public void eliminarCurso(Long id) {
        if (!cursoRepository.existsById(id)) {
            throw new NoSuchElementException("No se encontró el curso con ID: " + id);
        }
        if (matriculaRepository.existsByIdCursoId(id)) {
            throw new IllegalStateException("No se puede eliminar el curso porque tiene estudiantes matriculados asociados.");
        }
        cursoRepository.deleteById(id);
    }
}
