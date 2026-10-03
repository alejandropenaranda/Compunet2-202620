package com.compunet.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.compunet.springboot.model.Matricula;
import com.compunet.springboot.model.MatriculaId;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, MatriculaId> {

}

