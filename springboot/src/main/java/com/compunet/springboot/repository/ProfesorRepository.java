package com.compunet.springboot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.compunet.springboot.model.Profesor;

@Repository 
public interface ProfesorRepository extends JpaRepository<Profesor, Long> {

    Optional<Profesor> findByUsuario_Id(Long usuarioId);

}

