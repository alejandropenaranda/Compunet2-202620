package com.compunet.springboot.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    /**
     * Consulta todos los usuarios activos del sistema.
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuariosActivos() {
        return usuarioRepository.findAll();
    }

    /**
     * Ejercicio 1: Buscar usuario por correo institucional exacto.
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerPorCorreo(String correo) {
        return usuarioRepository.findByCorreoInstitucional(correo);
    }

    /**
     * Ejercicio 2: Verificar si existe un usuario por correo institucional.
     */
    @Transactional(readOnly = true)
    public boolean existeCorreo(String correo) {
        return usuarioRepository.existsByCorreoInstitucional(correo);
    }

    /**
     * Ejercicio 11: Listar usuarios activos por nombre de rol.
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuariosActivosPorRol(String rol) {
        return usuarioRepository.findByRoles_NombreIgnoreCaseAndActiveTrue(rol);
    }

    /**
     * Registra un nuevo usuario validando que el correo no se encuentre registrado
     * previamente.
     */
    @Transactional
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
}
