package com.compunet.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.compunet.springboot.model.Profesor;

@Repository 
public interface ProfesorRepository extends JpaRepository<Profesor, Long> {
    
    public List<Profesor> findAll();

    // Ejercicio 3: Profesores activos por departamento
    List<Profesor> findByDepartamentoIgnoreCaseAndActiveTrue(String departamento);

    // Ejercicio 6: Profesores por especialidad ordenados por apellido
    List<Profesor> findByEspecialidadIgnoreCaseOrderByApellidoAsc(String especialidad);



    //Ejercicio dificil #1

    List <Profesor> findDistinctByDepartamentoIgnoreCaseAndCursos_CreditosGreaterThanEqualAndActiveTrueOrderByApellidoAscNombreAsc(String depto, Integer creditos);
}
