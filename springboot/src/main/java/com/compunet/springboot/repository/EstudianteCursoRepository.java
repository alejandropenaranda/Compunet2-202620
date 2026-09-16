package com.compunet.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.compunet.springboot.model.EstudianteCurso;
import com.compunet.springboot.model.EstudianteCursoId;

@Repository
public interface EstudianteCursoRepository extends JpaRepository<EstudianteCurso, EstudianteCursoId> {

    List<EstudianteCurso> findByIdEstudianteId(Long estudianteId);

    List<EstudianteCurso> findByIdCursoId(Long cursoId);

    // Ejercicio 12: Verificar matrícula en tabla intermedia
    boolean existsById_EstudianteIdAndId_CursoId(Long estudianteId, Long cursoId);

    // Ejercicio difícil #3 - Parte A: Verificación de existencia cruzada
    boolean existsByEstudiante_ActiveTrueAndCurso_DepartamentoIgnoreCaseAndCurso_Profesor_Id(
        String departamentoCurso, 
        Long profesorId
    );

    // Ejercicio difícil #3 - Parte B: Conteo por coincidencia parcial y rango numérico
    long countByCurso_NombreContainingIgnoreCaseAndCurso_CreditosBetween(
        String subcadenaNombre, 
        int minCreditos, 
        int maxCreditos
    );

}
