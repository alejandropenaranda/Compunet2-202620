package com.compunet.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.compunet.springboot.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByCorreoInstitucional(String correoInstitucional);

    List<Usuario> findByRoles_NombreIgnoreCaseAndActiveTrue(String nombreRol);

}

