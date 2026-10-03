package com.compunet.springboot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.compunet.springboot.model.Profesor;
import com.compunet.springboot.model.Usuario;
import com.compunet.springboot.repository.ProfesorRepository;
import com.compunet.springboot.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfesorService {

    private final ProfesorRepository profesorRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Obtener todos los profesores registrados.
     */
    public List<Profesor> listarTodos() {
        return profesorRepository.findAll();
    }

    /**
     * Obtener un profesor por su identificador único (ID).
     */
    public Optional<Profesor> obtenerPorId(Long id) {
        return profesorRepository.findById(id);
    }

    /**
     * Obtener perfil de profesor por el ID del usuario asociado.
     */
    public Optional<Profesor> obtenerPorUsuarioId(Long usuarioId) {
        return profesorRepository.findByUsuario_Id(usuarioId);
    }

    /**
     * Registrar un perfil de profesor asociándolo a un usuario existente.
     */
    public Profesor registrarProfesor(Long usuarioId, String especialidad, String departamento) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el usuario con ID: " + usuarioId));

        if (profesorRepository.findByUsuario_Id(usuarioId).isPresent()) {
            throw new IllegalArgumentException("El usuario ya tiene un perfil docente asignado.");
        }

        Profesor profesor = new Profesor();
        profesor.setUsuario(usuario);
        profesor.setEspecialidad(especialidad);
        profesor.setDepartamento(departamento);

        return profesorRepository.save(profesor);
    }

    /**
     * Actualizar los datos del perfil de profesor.
     */
    public Profesor actualizarProfesor(Long id, String especialidad, String departamento) {
        Profesor profesorDb = profesorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el profesor con ID: " + id));

        profesorDb.setEspecialidad(especialidad);
        profesorDb.setDepartamento(departamento);

        return profesorRepository.save(profesorDb);
    }

    /**
     * Eliminar el perfil de profesor.
     */
    public void eliminarProfesor(Long id) {
        if (!profesorRepository.existsById(id)) {
            throw new IllegalArgumentException("No se encontró el profesor con ID: " + id);
        }
        profesorRepository.deleteById(id);
    }
}

