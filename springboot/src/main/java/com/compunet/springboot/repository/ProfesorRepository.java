package com.compunet.springboot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.compunet.springboot.model.Profesor;

@Repository 
public interface ProfesorRepository extends JpaRepository<Profesor, Long> {
    
    List<Profesor> findAll();

    Optional<Profesor> findByUsuario_Id(Long usuarioId);

    // Ejercicio 3: Profesores activos por departamento
    List<Profesor> findByDepartamentoIgnoreCaseAndUsuario_ActiveTrue(String departamento);

    // Ejercicio 6: Profesores por especialidad ordenados por apellido del usuario
    List<Profesor> findByEspecialidadIgnoreCaseOrderByUsuario_ApellidoAsc(String especialidad);

    // Ejercicio difícil #1
    List<Profesor> findDistinctByDepartamentoIgnoreCaseAndCursos_CreditosGreaterThanEqualAndUsuario_ActiveTrueOrderByUsuario_ApellidoAscUsuario_NombreAsc(String depto, Integer creditos);
}
