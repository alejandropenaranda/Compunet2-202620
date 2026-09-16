# Asignación: Query Methods y Consultas en Spring Data JPA

**Curso:** Computación en Internet II (Compunet II) -- Universidad ICESI  
**Instrucciones:** Para cada ejercicio:
1. Declara el método de consulta en la interfaz del **Repositorio**.
2. Implementa un método en el **Service** que inyecte el repositorio e invoque la consulta.

---

### Ejercicio 1: Buscar usuario por correo
* **Requerimiento:** Buscar un usuario por su `correoInstitucional` exacto.
* **Repositorio:** `UsuarioRepository`
* **Service:** `UsuarioService`
```java
// Repositorio:
Optional<Usuario> findByCorreoInstitucional(String correoInstitucional);

// Service:
public Optional<Usuario> obtenerPorCorreo(String correo) {
    return usuarioRepository.findByCorreoInstitucional(correo);
}
```

---

### Ejercicio 2: Verificar existencia de correo
* **Requerimiento:** Comprobar si ya existe un usuario con un `correoInstitucional` determinado (retorna `boolean`).
* **Repositorio:** `UsuarioRepository`
* **Service:** `UsuarioService`
```java
// Repositorio:
boolean existsByCorreoInstitucional(String correoInstitucional);

// Service:
public boolean existeCorreo(String correo) {
    return usuarioRepository.existsByCorreoInstitucional(correo);
}
```

---

### Ejercicio 3: Profesores activos por departamento
* **Requerimiento:** Obtener los profesores activos (`active = true`) que pertenezcan a un `departamento` específico, sin distinguir mayúsculas de minúsculas.
* **Repositorio:** `ProfesorRepository`
* **Service:** `ProfesorService`
```java
// Repositorio:
List<Profesor> findByDepartamentoIgnoreCaseAndActiveTrue(String departamento);

// Service:
public List<Profesor> listarProfesoresActivosPorDepartamento(String depto) {
    return profesorRepository.findByDepartamentoIgnoreCaseAndActiveTrue(depto);
}
```

---

### Ejercicio 4: Cursos por rango de créditos
* **Requerimiento:** Obtener los cursos cuya cantidad de `creditos` se encuentre dentro de un rango (`min` y `max`).
* **Repositorio:** `CursoRepository`
* **Service:** `CursoService`
```java
// Repositorio:
List<Curso> findByCreditosBetween(int minCreditos, int maxCreditos);

// Service:
public List<Curso> listarPorRangoCreditos(int min, int max) {
    return cursoRepository.findByCreditosBetween(min, max);
}
```

---

### Ejercicio 5: Buscar cursos por coincidencia en el nombre
* **Requerimiento:** Buscar cursos cuyo `nombre` contenga un texto dado, sin distinguir mayúsculas de minúsculas.
* **Repositorio:** `CursoRepository`
* **Service:** `CursoService`
```java
// Repositorio:
List<Curso> findByNombreContainingIgnoreCase(String fragmentoNombre);

// Service:
public List<Curso> buscarCursosPorNombre(String texto) {
    return cursoRepository.findByNombreContainingIgnoreCase(texto);
}
```

---

### Ejercicio 6: Profesores por especialidad ordenados por apellido
* **Requerimiento:** Obtener los profesores de una `especialidad` dada (sin distinguir mayúsculas) ordenados alfabéticamente por su `apellido` de forma ascendente.
* **Repositorio:** `ProfesorRepository`
* **Service:** `ProfesorService`
```java
// Repositorio:
List<Profesor> findByEspecialidadIgnoreCaseOrderByApellidoAsc(String especialidad);

// Service:
public List<Profesor> listarPorEspecialidadOrdenados(String especialidad) {
    return profesorRepository.findByEspecialidadIgnoreCaseOrderByApellidoAsc(especialidad);
}
```

---

### Ejercicio 7: Estudiantes por dominio de correo
* **Requerimiento:** Obtener los estudiantes cuyo `correoInstitucional` termine con una cadena o dominio dado (ej. `"@icesi.edu.co"`), ignorando mayúsculas/minúsculas.
* **Repositorio:** `EstudianteRepository`
* **Service:** `EstudianteService`
```java
// Repositorio:
List<Estudiante> findByCorreoInstitucionalEndingWithIgnoreCase(String sufijoDominio);

// Service:
public List<Estudiante> filtrarPorDominio(String dominio) {
    return estudianteRepository.findByCorreoInstitucionalEndingWithIgnoreCase(dominio);
}
```

---

### Ejercicio 8: Conteo de estudiantes activos
* **Requerimiento:** Contar el total de estudiantes cuyo estado sea activo (`active = true`).
* **Repositorio:** `EstudianteRepository`
* **Service:** `EstudianteService`
```java
// Repositorio:
long countByActiveTrue();

// Service:
public long contarEstudiantesActivos() {
    return estudianteRepository.countByActiveTrue();
}
```

---

### Ejercicio 9: Cursos asignados a un profesor (ManyToOne)
* **Requerimiento:** Obtener todos los cursos que dicta un profesor a partir del `id` del profesor.
* **Repositorio:** `CursoRepository`
* **Service:** `CursoService`
```java
// Repositorio:
List<Curso> findByProfesor_Id(Long profesorId);

// Service:
public List<Curso> listarCursosDeProfesor(Long profesorId) {
    return cursoRepository.findByProfesor_Id(profesorId);
}
```

---

### Ejercicio 10: Cursos según el departamento del profesor
* **Requerimiento:** Obtener los cursos cuyo profesor pertenezca a un `departamento` determinado (navegando la relación `profesor`), sin distinguir mayúsculas.
* **Repositorio:** `CursoRepository`
* **Service:** `CursoService`
```java
// Repositorio:
List<Curso> findByProfesor_DepartamentoIgnoreCase(String deptoProfesor);

// Service:
public List<Curso> listarCursosPorDepartamentoDelProfesor(String depto) {
    return cursoRepository.findByProfesor_DepartamentoIgnoreCase(depto);
}
```

---

### Ejercicio 11: Usuarios activos por nombre de rol (ManyToMany)
* **Requerimiento:** Obtener todos los usuarios activos (`active = true`) que tengan asignado un rol con un `nombre` dado (navegando la relación `roles`), sin distinguir mayúsculas.
* **Repositorio:** `UsuarioRepository`
* **Service:** `UsuarioService`
```java
// Repositorio:
List<Usuario> findByRoles_NombreIgnoreCaseAndActiveTrue(String nombreRol);

// Service:
public List<Usuario> listarUsuariosActivosPorRol(String rol) {
    return usuarioRepository.findByRoles_NombreIgnoreCaseAndActiveTrue(rol);
}
```

---

### Ejercicio 12: Verificar matrícula en tabla intermedia
* **Requerimiento:** Comprobar si existe un registro de matrícula para un `estudianteId` y un `cursoId` dados en `EstudianteCurso` (retorna `boolean`).
* **Repositorio:** `EstudianteCursoRepository`
* **Service:** `MatriculaService`
```java
// Repositorio:
boolean existsById_EstudianteIdAndId_CursoId(Long estudianteId, Long cursoId);

// Service:
public boolean estaMatriculado(Long estudianteId, Long cursoId) {
    return estudianteCursoRepository.existsById_EstudianteIdAndId_CursoId(estudianteId, cursoId);
}
```

---

### Ejercicio 13: Cursos por créditos mínimos ordenados descendentemente
* **Requerimiento:** Obtener los cursos con `creditos` mayores o iguales a un valor mínimo, ordenados por créditos de mayor a menor.
* **Repositorio:** `CursoRepository`
* **Service:** `CursoService`
```java
// Repositorio:
List<Curso> findByCreditosGreaterThanEqualOrderByCreditosDesc(int creditosMinimos);

// Service:
public List<Curso> obtenerCursosPorCreditosMinimos(int creditosMin) {
    return cursoRepository.findByCreditosGreaterThanEqualOrderByCreditosDesc(creditosMin);
}
```

---

### Ejercicio 14: Permisos de un rol (ManyToMany inversa)
* **Requerimiento:** Obtener todos los permisos asignados a un rol buscando por el `nombre` del rol (navegando la relación `roles` desde `Permiso`), sin distinguir mayúsculas.
* **Repositorio:** `PermisoRepository`
* **Service:** `PermisoService`
```java
// Repositorio:
List<Permiso> findByRoles_NombreIgnoreCase(String nombreRol);

// Service:
public List<Permiso> listarPermisosDeRol(String rol) {
    return permisoRepository.findByRoles_NombreIgnoreCase(rol);
}
```

---

### Ejercicio 15: Estudiantes de un curso con `@Query` (JPQL y Native)
* **Requerimiento:** Obtener los estudiantes activos (`active = true`) matriculados en un curso (`cursoId`), ordenados por `apellido` ascendente.
  - Implementar una versión con **JPQL**.
  - Implementar una versión con **Native Query** (`nativeQuery = true`).
* **Repositorio:** `EstudianteRepository`
* **Service:** `EstudianteService`
```java
// Repositorio (JPQL):
@Query("SELECT ec.estudiante FROM EstudianteCurso ec WHERE ec.curso.id = :cursoId AND ec.estudiante.active = true ORDER BY ec.estudiante.apellido ASC")
List<Estudiante> buscarEstudiantesPorCursoJPQL(@Param("cursoId") Long cursoId);

// Repositorio (Native SQL):
@Query(value = "SELECT e.* FROM Estudiante e INNER JOIN estudiante_curso ec ON e.id = ec.estudiante_id WHERE ec.curso_id = :cursoId AND e.active = true ORDER BY e.apellido ASC", nativeQuery = true)
List<Estudiante> buscarEstudiantesPorCursoNativo(@Param("cursoId") Long cursoId);

// Service:
public List<Estudiante> listarEstudiantesDeCursoJPQL(Long cursoId) {
    return estudianteRepository.buscarEstudiantesPorCursoJPQL(cursoId);
}

public List<Estudiante> listarEstudiantesDeCursoNativo(Long cursoId) {
    return estudianteRepository.buscarEstudiantesPorCursoNativo(cursoId);
}
```
