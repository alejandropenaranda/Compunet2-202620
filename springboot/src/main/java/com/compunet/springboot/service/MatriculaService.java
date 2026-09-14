package com.compunet.springboot.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
     * Proceso atómico de matrícula: si cualquier condición falla,
     * no se guarda ningún registro parcial.
     */
    @Transactional(rollbackFor = Exception.class)
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

        // Simulación de validación tardía o fallo del sistema
        if (curso.getCreditos() > 4) {
            // Esta excepción disparará el ROLLBACK completo. La matrícula recién guardada
            // NO persistirá en la base de datos.
            throw new RuntimeException("Límite de créditos excedido: Se requiere aprobación del comité.");
        }

        return guardado;
    }
}