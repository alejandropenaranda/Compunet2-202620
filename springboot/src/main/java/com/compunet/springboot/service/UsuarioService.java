package com.compunet.springboot.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
     * Consulta todos los usuarios activos del sistema.
     */
    public List<Usuario> listarUsuariosActivos() {
        return usuarioRepository.findAll();
    }

    /**
     * Ejercicio 1: Buscar usuario por correo institucional exacto.
     */
    public Optional<Usuario> obtenerPorCorreo(String correo) {
        return usuarioRepository.findByCorreoInstitucional(correo);
    }

    /**
     * Ejercicio 2: Verificar si existe un usuario por correo institucional.
     */
    public boolean existeCorreo(String correo) {
        return usuarioRepository.existsByCorreoInstitucional(correo);
    }

    /**
     * Ejercicio 11: Listar usuarios activos por nombre de rol.
     */
    public List<Usuario> listarUsuariosActivosPorRol(String rol) {
        return usuarioRepository.findByRoles_NombreIgnoreCaseAndActiveTrue(rol);
    }

    /**
     * Registra un nuevo usuario validando que el correo no se encuentre registrado
     * previamente.
     */
    public Usuario registrarUsuario(Usuario usuario, String nombreRolInicial) {
        // Regla de Negocio 1: Correo único
        if (usuarioRepository.existsByCorreoInstitucional(usuario.getCorreoInstitucional())) {
            throw new IllegalArgumentException("El correo institucional ya se encuentra registrado: "
                    + usuario.getCorreoInstitucional());
        }

        // Regla de Negocio 2: Asignación de rol base
        Rol rol = rolRepository.findByNombre(nombreRolInicial)
                .orElseThrow(() -> new IllegalStateException("El rol especificado no existe: " + nombreRolInicial));

        if (usuario.getRoles() == null) {
            usuario.setRoles(new ArrayList<>());
        }

        usuario.getRoles().add(rol);
        usuario.setActive(true);

        return usuarioRepository.save(usuario);
    }

    /**
     * Busca un usuario por su identificador único (ID).
     */
    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Actualiza los datos de un usuario existente aplicando reglas de negocio.
     */
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
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

        usuarioDb.setActive(usuarioActualizado.isActive());

        return usuarioRepository.save(usuarioDb);
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
}
