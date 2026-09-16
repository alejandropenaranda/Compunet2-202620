package com.compunet.springboot.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.compunet.springboot.model.Curso;

@Repository 
public interface CursoRepository extends JpaRepository<Curso, Long> {

// 1. Búsqueda exacta por departamento
    List<Curso> findByDepartamento(String departamento);

    // 2. Búsqueda por rango de créditos
    List<Curso> findByCreditosBetween(int minCreditos, int maxCreditos);

    // 3. Búsqueda por coincidencia de texto en el nombre (insensible a mayúsculas)
    List<Curso> findByNombreContainingIgnoreCase(String fragmentoNombre);

    // 4. Búsqueda combinada con ordenamiento
    List<Curso> findByDepartamentoOrderByCreditosDesc(String departamento);

    // 5. Verificación de existencia
    boolean existsByNombreIgnoreCase(String nombre);



    // PAGIONACIÓN Y ORDENAMIENTO

     // Spring Data inyecta automáticamente LIMIT y OFFSET según el Pageable
    Page<Curso> findByDepartamento(String departamento, Pageable pageable);

    // Ejercicio 9: Cursos asignados a un profesor (ManyToOne)
    List<Curso> findByProfesor_Id(Long profesorId);

    // Ejercicio 10: Cursos según el departamento del profesor
    List<Curso> findByProfesor_DepartamentoIgnoreCase(String deptoProfesor);

    // Ejercicio 13: Cursos por créditos mínimos ordenados descendentemente
    List<Curso> findByCreditosGreaterThanEqualOrderByCreditosDesc(int creditosMinimos);

    //Ejercicio difil # 4
    List <Curso> findTop5ByDepartamentoInAndProfesor_ApellidoIgnoreCaseAndEstudianteCursos_Estudiante_IdInOrderByCreditosDesc(Collection<String> Deptos, String apellido, Collection<Long> Ids);
}
