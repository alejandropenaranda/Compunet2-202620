package com.compunet.springboot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.compunet.springboot.model.Permiso;
import com.compunet.springboot.model.Rol;
import com.compunet.springboot.repository.PermisoRepository;
import com.compunet.springboot.repository.RolRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;

    /**
     * Obtener todos los roles registrados en el sistema.
     */
    public List<Rol> listarTodos() {
        return rolRepository.findAll();
    }

    /**
     * Obtener un rol por su ID.
     */
    public Optional<Rol> obtenerPorId(Long id) {
        return rolRepository.findById(id);
    }

    /**
     * Obtener un rol por su nombre exacto.
     */
    public Optional<Rol> obtenerPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }

    /**
     * Registrar un nuevo rol en el sistema.
     */
    public Rol registrarRol(Rol rol) {
        if (rolRepository.existsByNombre(rol.getNombre())) {
            throw new IllegalArgumentException("Ya existe un rol registrado con el nombre: " + rol.getNombre());
        }
        return rolRepository.save(rol);
    }

    /**
     * Actualizar los datos de un rol existente.
     */
    public Rol actualizarRol(Long id, Rol rolActualizado) {
        Rol rolDb = rolRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el rol con ID: " + id));

        if (!rolDb.getNombre().equalsIgnoreCase(rolActualizado.getNombre())
                && rolRepository.existsByNombre(rolActualizado.getNombre())) {
            throw new IllegalArgumentException("Ya existe un rol registrado con el nombre: " + rolActualizado.getNombre());
        }

        rolDb.setNombre(rolActualizado.getNombre());
        rolDb.setDescripcion(rolActualizado.getDescripcion());

        return rolRepository.save(rolDb);
    }

    /**
     * Asignar un permiso a un rol.
     */
    public Rol agregarPermisoARol(Long rolId, Long permisoId) {
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el rol con ID: " + rolId));
        Permiso permiso = permisoRepository.findById(permisoId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el permiso con ID: " + permisoId));

        if (!rol.getPermisos().contains(permiso)) {
            rol.getPermisos().add(permiso);
            return rolRepository.save(rol);
        }
        return rol;
    }

    /**
     * Remover un permiso de un rol.
     */
    public Rol removerPermisoDeRol(Long rolId, Long permisoId) {
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el rol con ID: " + rolId));
        Permiso permiso = permisoRepository.findById(permisoId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el permiso con ID: " + permisoId));

        rol.getPermisos().remove(permiso);
        return rolRepository.save(rol);
    }

    /**
     * Eliminar un rol por su ID.
     */
    public void eliminarRol(Long id) {
        if (!rolRepository.existsById(id)) {
            throw new IllegalArgumentException("No se encontró el rol con ID: " + id);
        }
        rolRepository.deleteById(id);
    }
}
