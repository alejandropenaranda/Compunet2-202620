package com.compunet.springboot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.compunet.springboot.model.Estudiante;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    Optional<Estudiante> findByCorreoInstitucional(String correoInstitucional);

    // Ejercicio 7: Estudiantes por dominio de correo
    List<Estudiante> findByCorreoInstitucionalEndingWithIgnoreCase(String sufijoDominio);

    // Ejercicio 8: Conteo de estudiantes activos
    long countByActiveTrue();

    // Ejercicio 15 (JPQL): Estudiantes activos de un curso ordenados por apellido
    @Query("SELECT ec.estudiante FROM EstudianteCurso ec WHERE ec.curso.id = :cursoId AND ec.estudiante.active = true ORDER BY ec.estudiante.apellido ASC")
    List<Estudiante> buscarEstudiantesPorCursoJPQL(@Param("cursoId") Long cursoId);

    // Ejercicio 15 (Native Query): Estudiantes activos de un curso ordenados por apellido
    @Query(value = "SELECT e.* FROM Estudiante e INNER JOIN estudiante_curso ec ON e.id = ec.estudiante_id WHERE ec.curso_id = :cursoId AND e.active = true ORDER BY e.apellido ASC", nativeQuery = true)
    List<Estudiante> buscarEstudiantesPorCursoNativo(@Param("cursoId") Long cursoId);


    //Ejercicio difil 2:

    List <Estudiante> findDistinctByActiveTrueAndCorreoInstitucionalEndingWithAndEstudianteCursos_Curso_Profesor_EspecialidadIgnoreCaseOrderByApellidoAscNombreAsc(String dominio, String espcialidad);

}
