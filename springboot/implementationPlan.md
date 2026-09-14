# Plan de Implementación de Clase: Services, Query Methods y Native Queries en Spring Data JPA

**Asignatura:** Computación en Red II (Compunet II)  
**Institución:** Universidad ICESI  
**Frameworks:** Spring Boot 4.x / 3.x, Spring Data JPA, Hibernate, H2 / PostgreSQL  
**Asignación de Clase (Estudiantes):** [asignacion_query_methods.md](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/asignacion_query_methods.md)  
**Guía con Solucionario (Docente):** [ejercicios_query_methods.md](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/ejercicios_query_methods.md)  

---

## 1. Objetivos Pedagógicos de la Sesión

Esta sesión presencial tiene como propósito central que los estudiantes dominen el **diseño y consumo de consultas avanzadas** a través de una **arquitectura desacoplada en capas**:

### 🎯 Núcleo Presencial de la Clase:
1. **La Capa de Servicios (`@Service`):** Aplicar el principio de responsabilidad única (SRP), orquestar múltiples repositorios, ejecutar validaciones de dominio y exponer una interfaz limpia hacia los controladores HTTP.
2. **Consultas Derivadas (Query Methods por Convención):** Comprender el mecanismo de introspección de Spring Data JPA para derivar consultas SQL/JPQL a partir de firmas de métodos (`findBy...`, `existsBy...`, `countBy...`, operadores lógicos, rangos y navegación por asociaciones).
3. **Consultas con `@Query` (JPQL y Native Queries):** Identificar cuándo la convención resulta insuficiente y dominar la creación de consultas orientadas a objetos (JPQL) y consultas SQL nativas del motor, gestionando parámetros con `@Param` y actualizaciones con `@Modifying`.
4. **Desarrollo Guiado en Clase:** Resolver en vivo una serie progresiva de ejercicios prácticos basados en el dominio del proyecto (`Usuario`, `Profesor`, `Curso`, `Estudiante`, `Rol`, `Permiso`).

### 📚 Módulo de Estudio Autónomo (Anexos al final):
- **Gestión Transaccional (`@Transactional` y Rollback ACID):** Comprender proxies de Spring AOP, garantías ACID y políticas de rollback ante checked/unchecked exceptions.
- **Paginación y Ordenamiento (`Pageable`, `Page`, `Slice`):** Fragmentar conjuntos de datos masivos y optimizar el consumo de memoria JVM.

---

## 2. Arquitectura Multicapa y Flujo de Consultas

El flujo de ejecución debe mantener las fronteras de cada componente estrictamente delimitadas:

```
[ Cliente HTTP (Postman / Frontend) ]
                │  1. Petición HTTP (GET / POST)
                ▼
┌────────────────────────────────────────┐
│        Capa de Controladores           │  <- @RestController
│  - Mapea rutas y parámetros (@Param).  │  <- Valida sintaxis y formato.
│  - No contiene lógica de negocio.      │  <- Retorna ResponseEntity<T>.
└──────────────────┬─────────────────────┘
                   │  2. Invoca método de negocio
                   ▼
┌────────────────────────────────────────┐
│          Capa de Servicios             │  <- @Service
│  - Reglas de validación de dominio.    │  <- Orquesta repositorios.
│  - Invoca Query Methods y @Query.      │  <- Mapea a DTOs / Excepciones.
└──────────────────┬─────────────────────┘
                   │  3. Invoca método de persistencia
                   ▼
┌────────────────────────────────────────┐
│        Capa de Persistencia            │  <- Interfaces @Repository
│  - Métodos CRUD básicos de JPA.        │  <- Query Methods derivados.
│  - JPQL y Native Queries (@Query).     │  <- Ejecución contra BD.
└──────────────────┬─────────────────────┘
                   │  4. Sentencia SQL generada (JDBC / HikariCP)
                   ▼
┌────────────────────────────────────────┐
│       Base de Datos Relacional         │  <- H2 en memoria / PostgreSQL
└────────────────────────────────────────┘
```

---

## 3. Módulo 1: La Capa de Servicios (`@Service`) e Invocación de Repositorios

### 3.1. Responsabilidades del `@Service`
- **Desacoplamiento total:** El controlador desconoce cómo se almacenan o consultan los datos; solo interactúa con el contrato del servicio.
- **Validaciones de negocio previas:** Comprobar estados (`active`), verificar unicidad o validar reglas antes de invocar las consultas.
- **Inyección por constructor:** Utilizar atributos `private final` combinados con `@RequiredArgsConstructor` de Lombok en lugar de `@Autowired` sobre campos. Esto facilita el testing unitario con Mockito y previene nulidades accidentales.

### 3.2. Snippet de Ejemplo: `ProfesorService` consumiendo `ProfesorRepository` y `CursoRepository`

```java
package com.compunet.springboot.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.compunet.springboot.model.Profesor;
import com.compunet.springboot.model.Curso;
import com.compunet.springboot.repository.ProfesorRepository;
import com.compunet.springboot.repository.CursoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfesorService {

    private final ProfesorRepository profesorRepository;
    private final CursoRepository cursoRepository;

    @Transactional(readOnly = true)
    public List<Profesor> listarProfesoresPorDepartamento(String departamento) {
        if (departamento == null || departamento.isBlank()) {
            throw new IllegalArgumentException("El departamento no puede estar vacío");
        }
        return profesorRepository.findByDepartamentoIgnoreCaseAndActiveTrue(departamento);
    }

    @Transactional(readOnly = true)
    public List<Curso> listarCursosDeProfesor(Long profesorId) {
        if (!profesorRepository.existsByIdAndActiveTrue(profesorId)) {
            throw new EntityNotFoundException("El profesor no existe o se encuentra inactivo");
        }
        return cursoRepository.findByProfesor_IdOrderByNombreAsc(profesorId);
    }
}
```

---

## 4. Módulo 2: Consultas Derivadas (Query Methods por Convención)

Spring Data JPA analiza el nombre de cada método en la interfaz del repositorio al iniciar el contexto de la aplicación, construyendo la consulta correspondiente sin requerir código SQL manual.

### 4.1. Catálogo de Palabras Clave y Operadores

| Prefijo / Palabra Clave | Ejemplo de Método Java | Equivalente SQL / JPQL |
| :--- | :--- | :--- |
| `findBy...` | `findByCorreoInstitucional(String correo)` | `WHERE correo_institucional = ?` |
| `existsBy...` | `existsByCorreoInstitucional(String correo)` | `SELECT CASE WHEN COUNT(x)>0 THEN TRUE...` |
| `countBy...` | `countByActiveTrue()` | `SELECT COUNT(x) WHERE active = TRUE` |
| `deleteBy...` | `deleteByActiveFalse()` | `DELETE FROM entidad WHERE active = FALSE` |
| `And` / `Or` | `findByDepartamentoAndActiveTrue(String d)` | `WHERE departamento = ? AND active = TRUE` |
| `Between` | `findByCreditosBetween(int min, int max)` | `WHERE creditos BETWEEN ? AND ?` |
| `GreaterThan` / `LessThan` | `findByCreditosGreaterThanEqual(int min)` | `WHERE creditos >= ?` |
| `Containing` / `StartingWith` | `findByNombreContainingIgnoreCase(String t)` | `WHERE UPPER(nombre) LIKE UPPER('%t%')` |
| `OrderBy...Asc/Desc` | `findByActiveTrueOrderByApellidoAsc()` | `WHERE active = TRUE ORDER BY apellido ASC` |
| `Relación Navegada (_)` | `findByProfesor_Departamento(String d)` | `JOIN profesor p WHERE p.departamento = ?` |

### 4.2. Snippet de Ejemplo: `CursoRepository`

```java
package com.compunet.springboot.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.compunet.springboot.model.Curso;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    // 1. Filtrado exacto con descarte de mayúsculas/minúsculas
    List<Curso> findByDepartamentoIgnoreCase(String departamento);

    // 2. Coincidencia parcial de texto en el nombre
    List<Curso> findByNombreContainingIgnoreCase(String fragmento);

    // 3. Rango de créditos y ordenamiento
    List<Curso> findByCreditosBetweenOrderByCreditosDesc(int min, int max);

    // 4. Navegación por relación ManyToOne (Profesor)
    List<Curso> findByProfesor_Id(Long profesorId);

    // 5. Navegación por atributo de la relación ManyToOne
    List<Curso> findByProfesor_DepartamentoIgnoreCase(String deptoProfesor);

    // 6. Verificación de existencia combinada
    boolean existsByNombreIgnoreCaseAndDepartamentoIgnoreCase(String nombre, String departamento);
}
```

---

## 5. Módulo 3: Consultas Avanzadas con `@Query` (JPQL y Native Queries)

### 5.1. ¿Cuándo usar `@Query` en lugar de Query Methods?
- Cuando el nombre del método resulta excesivamente largo e ilegible (`findByNombreContainingAndDepartamentoAndCreditosGreaterThan...`).
- Cuando se requieren **uniones complejas (`JOIN`, `LEFT JOIN FETCH`)** entre múltiples entidades.
- Cuando se necesitan **funciones de agregación** (`SUM`, `AVG`, `GROUP BY`, `HAVING`).
- Cuando se precisa aprovechar sintaxis específica del motor de base de datos (PostgreSQL: `JSONB`, `ILIKE`, CTEs).

### 5.2. Comparativa: JPQL vs Native Query

| Dimensión | JPQL (Java Persistence Query Language) | Native Query (SQL Nativo) |
| :--- | :--- | :--- |
| **Entidades / Tablas** | Trabaja con clases Java (`Curso c`, `Profesor p`). | Trabaja con nombres de tablas (`curso`, `profesor`). |
| **Portabilidad** | 100% portable entre H2, PostgreSQL, Oracle, etc. | Acoplado al dialecto SQL específico configurado. |
| **Validación Sintáctica** | Se valida al compilar/arrancar el contexto de Spring. | Se valida en tiempo de ejecución al invocar el método. |
| **Relaciones** | Navega directamente mediante atributos JPA (`c.profesor`). | Requiere `JOIN` manual con llaves foráneas (`ON c.profesor_id = p.id`). |

### 5.3. Modificaciones Masivas con `@Modifying`
Para sentencias `UPDATE` o `DELETE` directas en base de datos:
- Es obligatorio agregar `@Modifying` sobre el método.
- Debe ejecutarse en el marco de una transacción (`@Transactional`).
- El tipo de retorno debe ser `int` o `void`, indicando el número de registros afectados.

### 5.4. Snippet de Ejemplo: `UsuarioRepository` y `@Query`

```java
package com.compunet.springboot.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.compunet.springboot.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // 1. JPQL: Join explícito entre Usuario y Rol
    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE r.nombre = :rol AND u.active = true")
    List<Usuario> buscarActivosPorRol(@Param("rol") String rol);

    // 2. JPQL: Agregación con COUNT
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.correoInstitucional LIKE %:dominio%")
    long contarUsuariosPorDominio(@Param("dominio") String dominio);

    // 3. Native Query: SQL directo con INNER JOIN sobre tablas físicas
    @Query(value = """
        SELECT u.* FROM usuario u
        INNER JOIN usuario_rol ur ON u.id = ur.usuario_id
        INNER JOIN rol r ON ur.rol_id = r.id
        WHERE LOWER(r.nombre) = LOWER(:rol) AND u.active = true
        """, nativeQuery = true)
    List<Usuario> buscarActivosPorRolNativo(@Param("rol") String rol);

}
```

---

## 6. Dinámica Práctica de Clase en Vivo (Hands-on)

### 📌 Metodología de la Sesión:
1. **Paso 1 (Exposición y Demostración - 25 min):** El docente presenta el flujo de llamadas `Controller -> Service -> Repository` y demuestra en consola la traducción de Query Methods con `spring.jpa.show-sql=true`.
2. **Paso 2 (Trabajo Práctico en Parejas - 45 min):** Los estudiantes abren el archivo de asignación [asignacion_query_methods.md](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/asignacion_query_methods.md) y resuelven los ejercicios directamente sobre los repositorios y servicios del proyecto (el docente puede apoyarse en la guía con soluciones [ejercicios_query_methods.md](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/ejercicios_query_methods.md)).
3. **Paso 3 (Puesta en Común y Casos Especiales - 20 min):** Revisión de la sintaxis con guión bajo (`_`) para relaciones compuestas y transición de un Query Method complejo a un `@Query` con JPQL.

