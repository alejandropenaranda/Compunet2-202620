package com.compunet.springboot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.compunet.springboot.model.Curso;
import com.compunet.springboot.model.Matricula;
import com.compunet.springboot.model.MatriculaId;
import com.compunet.springboot.model.Usuario;
import com.compunet.springboot.repository.CursoRepository;
import com.compunet.springboot.repository.MatriculaRepository;
import com.compunet.springboot.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;

    /**
     * Listar todas las matrículas registradas.
     */
    public List<Matricula> listarTodas() {
        return matriculaRepository.findAll();
    }

    /**
     * Obtener una matrícula por su ID compuesto.
     */
    public Optional<Matricula> obtenerPorId(Long usuarioId, Long cursoId) {
        return matriculaRepository.findById(new MatriculaId(usuarioId, cursoId));
    }

    /**
     * Realizar la matrícula de un usuario (estudiante) en un curso académico.
     */
    public Matricula matricularEstudianteEnCurso(Long usuarioId, Long cursoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));

        if (!usuario.isActive()) {
            throw new IllegalStateException("El estudiante está inactivo y no puede matricular cursos.");
        }

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado con ID: " + cursoId));

        MatriculaId idCompuesto = new MatriculaId(usuarioId, cursoId);
        if (matriculaRepository.existsById(idCompuesto)) {
            throw new IllegalStateException("El estudiante ya se encuentra matriculado en el curso " + curso.getNombre());
        }

        Matricula matricula = new Matricula(usuario, curso);
        return matriculaRepository.save(matricula);
    }

    /**
     * Cancelar o eliminar una matrícula.
     */
    public void desmatricularEstudiante(Long usuarioId, Long cursoId) {
        MatriculaId idCompuesto = new MatriculaId(usuarioId, cursoId);
        if (!matriculaRepository.existsById(idCompuesto)) {
            throw new IllegalArgumentException("No existe la matrícula del usuario " + usuarioId + " en el curso " + cursoId);
        }
        matriculaRepository.deleteById(idCompuesto);
    }

    /**
     * Ejercicio 12: Comprobar si existe un registro de matrícula para un usuario y un curso.
     */
    public boolean estaMatriculado(Long usuarioId, Long cursoId) {
        return matriculaRepository.existsById_UsuarioIdAndId_CursoId(usuarioId, cursoId);
    }
}