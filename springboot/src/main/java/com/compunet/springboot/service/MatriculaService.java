package com.compunet.springboot.service;

import org.springframework.stereotype.Service;
import com.compunet.springboot.model.Curso;
import com.compunet.springboot.model.Estudiante;
import com.compunet.springboot.model.EstudianteCurso;
import com.compunet.springboot.model.EstudianteCursoId;
import com.compunet.springboot.repository.CursoRepository;
import com.compunet.springboot.repository.EstudianteCursoRepository;
import com.compunet.springboot.repository.EstudianteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;
    private final EstudianteCursoRepository estudianteCursoRepository;

    /**
     * Proceso de matrícula de estudiante en curso.
     */
    public EstudianteCurso matricularEstudianteEnCurso(Long estudianteId, Long cursoId) throws Exception {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
            .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado con ID: " + estudianteId));

        if (!estudiante.isActive()) {
            throw new IllegalStateException("El estudiante está inactivo y no puede matricular cursos.");
        }

        Curso curso = cursoRepository.findById(cursoId)
            .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado con ID: " + cursoId));

        EstudianteCursoId idCompuesto = new EstudianteCursoId(estudianteId, cursoId);
        if (estudianteCursoRepository.existsById(idCompuesto)) {
            throw new IllegalStateException("El estudiante ya se encuentra matriculado en este curso.");
        }

        EstudianteCurso nuevaMatricula = new EstudianteCurso(estudiante, curso);
        EstudianteCurso guardado = estudianteCursoRepository.save(nuevaMatricula);

        return guardado;
    }

    /**
     * Ejercicio 12: Comprobar si existe un registro de matrícula para un estudiante y un curso.
     */
    public boolean estaMatriculado(Long estudianteId, Long cursoId) {
        return estudianteCursoRepository.existsById_EstudianteIdAndId_CursoId(estudianteId, cursoId);
    }
}