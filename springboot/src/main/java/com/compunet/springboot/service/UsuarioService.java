package com.compunet.springboot.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.compunet.springboot.model.Rol;
import com.compunet.springboot.model.Usuario;
import com.compunet.springboot.repository.RolRepository;
import com.compunet.springboot.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    /**
     * Consulta todos los usuarios del sistema.
     */
    public List<Usuario> listarUsuariosActivos() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca un usuario por su identificador único (ID).
     */
    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }


    /**
     * Ejercicio 11: Listar usuarios activos por nombre de rol.
     */
    public List<Usuario> listarUsuariosActivosPorRol(String rol) {
        return usuarioRepository.findByRoles_NombreIgnoreCaseAndActiveTrue(rol);
    }

    /**
     * Registra un nuevo usuario en el sistema con una lista de IDs de roles.
     */
    public Usuario registrarUsuario(Usuario usuario, List<Long> rolIds) {
        if (usuarioRepository.existsByCorreoInstitucional(usuario.getCorreoInstitucional())) {
            throw new IllegalArgumentException("El correo institucional ya se encuentra registrado: "
                    + usuario.getCorreoInstitucional());
        }

        if (rolIds != null && !rolIds.isEmpty()) {
            List<Rol> roles = rolRepository.findAllById(rolIds);
            usuario.setRoles(roles);
        } else if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            Rol rolDefault = rolRepository.findByNombre("ESTUDIANTE")
                    .orElseThrow(() -> new IllegalStateException("El rol por defecto no existe."));
            usuario.setRoles(new ArrayList<>(List.of(rolDefault)));
        }

        usuario.setActive(true);
        return usuarioRepository.save(usuario);
    }

    /**
     * Registra un nuevo usuario en el sistema.
     */
    public Usuario registrarUsuario(Usuario usuario) {
        return registrarUsuario(usuario, (List<Long>) null);
    }

    /**
     * Registra un nuevo usuario con un rol inicial opcional por nombre.
     */
    public Usuario registrarUsuario(Usuario usuario, String nombreRolInicial) {
        if (usuarioRepository.existsByCorreoInstitucional(usuario.getCorreoInstitucional())) {
            throw new IllegalArgumentException("El correo institucional ya se encuentra registrado: "
                    + usuario.getCorreoInstitucional());
        }

        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            String rolABuscar = (nombreRolInicial != null && !nombreRolInicial.isBlank()) ? nombreRolInicial : "ESTUDIANTE";
            Rol rol = rolRepository.findByNombre(rolABuscar)
                    .orElseThrow(() -> new IllegalStateException("El rol especificado no existe: " + rolABuscar));
            usuario.setRoles(new ArrayList<>(List.of(rol)));
        }

        usuario.setActive(true);
        return usuarioRepository.save(usuario);
    }

    /**
     * Actualiza los datos de un usuario existente aplicando reglas de negocio y asignando sus roles por IDs.
     * Nota: El estado (active) se gestiona exclusivamente a través de alternarEstado.
     */
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado, List<Long> rolIds) {
        Usuario usuarioDb = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el usuario con ID: " + id));

        // Regla de Negocio: Si cambia el correo institucional, validar que no esté ocupado por otro usuario
        if (!usuarioDb.getCorreoInstitucional().equalsIgnoreCase(usuarioActualizado.getCorreoInstitucional())
                && usuarioRepository.existsByCorreoInstitucional(usuarioActualizado.getCorreoInstitucional())) {
            throw new IllegalArgumentException("El nuevo correo institucional ya se encuentra registrado: "
                    + usuarioActualizado.getCorreoInstitucional());
        }

        usuarioDb.setNombre(usuarioActualizado.getNombre());
        usuarioDb.setApellido(usuarioActualizado.getApellido());
        usuarioDb.setCorreoInstitucional(usuarioActualizado.getCorreoInstitucional());

        if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isBlank()) {
            usuarioDb.setPassword(usuarioActualizado.getPassword());
        }

        if (rolIds != null && !rolIds.isEmpty()) {
            List<Rol> roles = rolRepository.findAllById(rolIds);
            usuarioDb.setRoles(roles);
        } else if (usuarioActualizado.getRoles() != null) {
            usuarioDb.setRoles(new ArrayList<>(usuarioActualizado.getRoles()));
        }

        return usuarioRepository.save(usuarioDb);
    }

    /**
     * Actualiza los datos de un usuario existente aplicando reglas de negocio.
     */
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        return actualizarUsuario(id, usuarioActualizado, null);
    }

    /**
     * Alterna el estado activo/inactivo de un usuario (Soft delete / Reactivación).
     */
    public Usuario alternarEstado(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el usuario con ID: " + id));
        usuario.setActive(!usuario.isActive());
        return usuarioRepository.save(usuario);
    }

    /**
     * Eliminar físicamente un usuario por ID.
     */
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("No se encontró el usuario con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}
