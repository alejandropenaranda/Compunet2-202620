package com.compunet.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.compunet.springboot.model.Profesor;

@Repository 
public interface ProfesorRepository extends JpaRepository<Profesor, Long> {
    
    public List<Profesor>findAll();
}
