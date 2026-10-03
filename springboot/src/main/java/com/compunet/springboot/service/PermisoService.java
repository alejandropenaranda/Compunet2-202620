package com.compunet.springboot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.compunet.springboot.model.Permiso;
import com.compunet.springboot.repository.PermisoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermisoService {

    private final PermisoRepository permisoRepository;

    /**
     * Obtener todos los permisos registrados.
     */
    public List<Permiso> listarTodos() {
        return permisoRepository.findAll();
    }

    /**
     * Obtener un permiso por su ID.
     */
    public Optional<Permiso> obtenerPorId(Long id) {
        return permisoRepository.findById(id);
    }

    /**
     * Registrar un nuevo permiso validando unicidad de nombre.
     */
    public Permiso registrarPermiso(Permiso permiso) {
        if (permisoRepository.existsByNombre(permiso.getNombre())) {
            throw new IllegalArgumentException("El permiso ya existe con el nombre: " + permiso.getNombre());
        }
        return permisoRepository.save(permiso);
    }

    /**
     * Actualizar los datos de un permiso existente.
     */
    public Permiso actualizarPermiso(Long id, Permiso permisoActualizado) {
        Permiso permisoDb = permisoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el permiso con ID: " + id));

        if (!permisoDb.getNombre().equalsIgnoreCase(permisoActualizado.getNombre())
                && permisoRepository.existsByNombre(permisoActualizado.getNombre())) {
            throw new IllegalArgumentException("Ya existe otro permiso con el nombre: " + permisoActualizado.getNombre());
        }

        permisoDb.setNombre(permisoActualizado.getNombre());
        permisoDb.setDescripcion(permisoActualizado.getDescripcion());

        return permisoRepository.save(permisoDb);
    }

    /**
     * Eliminar físicamente un permiso por ID.
     */
    public void eliminarPermiso(Long id) {
        if (!permisoRepository.existsById(id)) {
            throw new IllegalArgumentException("No se encontró el permiso con ID: " + id);
        }
        permisoRepository.deleteById(id);
    }

}

