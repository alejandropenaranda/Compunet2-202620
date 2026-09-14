package com.compunet.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.compunet.springboot.model.CursoMany;

@Repository
public interface CursoManyRepository extends JpaRepository<CursoMany, Long> {

}
