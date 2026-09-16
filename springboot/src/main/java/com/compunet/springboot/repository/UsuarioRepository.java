package com.compunet.springboot.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.compunet.springboot.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreoInstitucional(String correoInstitucional);

    boolean existsByCorreoInstitucional(String correoInstitucional);

    List<Usuario> findByRoles_NombreIgnoreCaseAndActiveTrue(String nombreRol);

   //Ejercicio dificil 5
    List <Usuario> findDistinctByActiveTrueAndRoles_NombreInAndRoles_Permisos_NombreIgnoreCaseOrderByApellidoAscNombreAsc(Collection<String> roles, String nombrePermiso);

}
