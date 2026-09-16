package com.compunet.springboot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.compunet.springboot.model.Permiso;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    Optional<Permiso> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    // Ejercicio 14: Permisos de un rol (ManyToMany inversa)
    List<Permiso> findByRoles_NombreIgnoreCase(String nombreRol);

}
