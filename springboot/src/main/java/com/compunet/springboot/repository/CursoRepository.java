package com.compunet.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.compunet.springboot.model.Curso;

@Repository 
public interface CursoRepository extends JpaRepository<Curso, Long> {

    List<Curso> findAll();
    
}
