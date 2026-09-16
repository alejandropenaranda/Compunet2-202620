package com.compunet.springboot.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compunet.springboot.model.Permiso;
import com.compunet.springboot.repository.PermisoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermisoService {

    private final PermisoRepository permisoRepository;

    @Transactional(readOnly = true)
    public List<Permiso> listarTodos() {
        return permisoRepository.findAll();
    }

    /**
     * Ejercicio 14: Obtener todos los permisos asignados a un rol buscando por nombre del rol.
     */
    @Transactional(readOnly = true)
    public List<Permiso> listarPermisosDeRol(String rol) {
        return permisoRepository.findByRoles_NombreIgnoreCase(rol);
    }
}
