package com.compunet.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.compunet.springboot.model.Matricula;
import com.compunet.springboot.model.MatriculaId;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, MatriculaId> {

    List<Matricula> findById_UsuarioId(Long usuarioId);

    List<Matricula> findById_CursoId(Long cursoId);

    // Ejercicio 12: Verificar matrícula en tabla intermedia
    boolean existsById_UsuarioIdAndId_CursoId(Long usuarioId, Long cursoId);

    // Ejercicio difícil #3 - Parte A: Verificación de existencia cruzada
    boolean existsByUsuario_ActiveTrueAndCurso_DepartamentoIgnoreCaseAndCurso_Profesor_Id(
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
