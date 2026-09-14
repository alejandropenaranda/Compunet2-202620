package com.compunet.springboot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.compunet.springboot.model.EstudianteMany;

@Repository
public interface EstudianteManyRepository extends JpaRepository<EstudianteMany, Long> {

    Optional<EstudianteMany> findByCorreoInstitucional(String correoInstitucional);

}
