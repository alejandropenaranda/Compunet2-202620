package com.compunet.springboot.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // =========================================================================
    // 1. MÉTODOS DE CONSULTA (READ)
    // =========================================================================

    /**
     * Consulta todos los usuarios del sistema.
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    /**
     * Consulta usuarios aplicando filtro opcional por estado activo/inactivo usando Java Streams.
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios(Boolean active) {
        if (active == null) {
            return usuarioRepository.findAll();
        }
        return usuarioRepository.findAll()
                .stream()
                .filter(u -> active == u.isActive())
                .toList();
    }

    /**
     * Busca un usuario por su identificador único (ID).
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Consulta usuarios activos filtrados por nombre de rol.
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuariosPorRol(String rol) {
        return usuarioRepository.findByRoles_NombreIgnoreCaseAndActiveTrue(rol);
    }

    // =========================================================================
    // 2. MÉTODOS DE CREACIÓN Y REGISTRO (CREATE)
    // =========================================================================

    /**
     * Registra un nuevo usuario en el sistema con asignación de roles mediante sus IDs numéricos (MVC tradicional).
     */
    @Transactional
    public Usuario registrarUsuario(Usuario usuario, List<Long> rolIds) {
        validarCorreoUnico(usuario.getCorreoInstitucional(), null);

        if (rolIds != null && !rolIds.isEmpty()) {
            List<Rol> roles = rolRepository.findAllById(rolIds);
            usuario.setRoles(roles);
        } else if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            usuario.setRoles(new ArrayList<>(List.of(obtenerRolDefault())));
        }

        usuario.setActive(true);
        return usuarioRepository.save(usuario);
    }

    /**
     * Registra un nuevo usuario en el sistema a partir de nombres de roles (Set<String> del DTO REST).
     * Itera sobre cada nombre, busca las entidades Rol reales en BD y las asigna.
     */
    @Transactional
    public Usuario registrarUsuario(Usuario usuario, Set<String> nombresRoles) {
        validarCorreoUnico(usuario.getCorreoInstitucional(), null);

        List<Rol> rolesAsignados = new ArrayList<>();
        if (nombresRoles != null && !nombresRoles.isEmpty()) {
            for (String nombreRol : nombresRoles) {
                rolRepository.findByNombre(nombreRol).ifPresent(rolesAsignados::add);
            }
        }

        if (rolesAsignados.isEmpty()) {
            rolesAsignados.add(obtenerRolDefault());
        }

        usuario.setRoles(rolesAsignados);
        usuario.setActive(true);
        return usuarioRepository.save(usuario);
    }

    /**
     * Registra un nuevo usuario en el sistema con el rol por defecto (sin lista de roles).
     */
    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        return registrarUsuario(usuario, (List<Long>) null);
    }

    // =========================================================================
    // 3. MÉTODOS DE ACTUALIZACIÓN (UPDATE)
    // =========================================================================

    /**
     * Actualiza los datos de un usuario existente asignando sus roles mediante IDs (MVC).
     */
    @Transactional
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado, List<Long> rolIds) {
        Usuario usuarioDb = obtenerUsuarioOExcepcion(id);
        validarCorreoUnico(usuarioActualizado.getCorreoInstitucional(), usuarioDb.getCorreoInstitucional());

        aplicarDatosBasicos(usuarioDb, usuarioActualizado);

        if (rolIds != null && !rolIds.isEmpty()) {
            List<Rol> roles = rolRepository.findAllById(rolIds);
            usuarioDb.setRoles(roles);
        } else if (usuarioActualizado.getRoles() != null && !usuarioActualizado.getRoles().isEmpty()) {
            usuarioDb.setRoles(new ArrayList<>(usuarioActualizado.getRoles()));
        }

        return usuarioRepository.save(usuarioDb);
    }

    /**
     * Actualiza los datos de un usuario existente resolviendo sus roles por nombres (Set<String> del DTO REST).
     */
    @Transactional
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado, Set<String> nombresRoles) {
        Usuario usuarioDb = obtenerUsuarioOExcepcion(id);
        validarCorreoUnico(usuarioActualizado.getCorreoInstitucional(), usuarioDb.getCorreoInstitucional());

        aplicarDatosBasicos(usuarioDb, usuarioActualizado);

        if (nombresRoles != null && !nombresRoles.isEmpty()) {
            List<Rol> rolesAsignados = new ArrayList<>();
            for (String nombreRol : nombresRoles) {
                rolRepository.findByNombre(nombreRol).ifPresent(rolesAsignados::add);
            }
            if (!rolesAsignados.isEmpty()) {
                usuarioDb.setRoles(rolesAsignados);
            }
        }

        return usuarioRepository.save(usuarioDb);
    }

    /**
     * Actualiza los datos de un usuario existente sin alterar sus roles previos.
     */
    @Transactional
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        return actualizarUsuario(id, usuarioActualizado, (List<Long>) null);
    }

    /**
     * Alterna el estado activo/inactivo de un usuario (Soft delete / Reactivación parcial).
     */
    @Transactional
    public Usuario alternarEstado(Long id) {
        Usuario usuario = obtenerUsuarioOExcepcion(id);
        usuario.setActive(!usuario.isActive());
        return usuarioRepository.save(usuario);
    }

    // =========================================================================
    // 4. MÉTODOS DE ELIMINACIÓN (DELETE)
    // =========================================================================

    /**
     * Elimina físicamente un usuario por su ID.
     */
    @Transactional
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("No se encontró el usuario con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    // =========================================================================
    // 5. MÉTODOS AUXILIARES Y REGLAS DE NEGOCIO PRIVADAS
    // =========================================================================

    private Usuario obtenerUsuarioOExcepcion(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el usuario con ID: " + id));
    }

    private void validarCorreoUnico(String nuevoCorreo, String correoActual) {
        if (correoActual == null || !correoActual.equalsIgnoreCase(nuevoCorreo)) {
            if (usuarioRepository.existsByCorreoInstitucional(nuevoCorreo)) {
                throw new IllegalArgumentException("El correo institucional ya se encuentra registrado: " + nuevoCorreo);
            }
        }
    }

    private void aplicarDatosBasicos(Usuario usuarioDb, Usuario datosNuevos) {
        usuarioDb.setNombre(datosNuevos.getNombre());
        usuarioDb.setApellido(datosNuevos.getApellido());
        usuarioDb.setCorreoInstitucional(datosNuevos.getCorreoInstitucional());

        if (datosNuevos.getPassword() != null && !datosNuevos.getPassword().isBlank()) {
            usuarioDb.setPassword(datosNuevos.getPassword());
        }
    }

    private Rol obtenerRolDefault() {
        return rolRepository.findByNombre("ESTUDIANTE")
                .orElseThrow(() -> new IllegalStateException("El rol por defecto ESTUDIANTE no existe."));
    }
}
